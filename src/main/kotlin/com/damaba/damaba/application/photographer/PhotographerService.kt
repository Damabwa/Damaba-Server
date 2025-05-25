package com.damaba.damaba.application.photographer

import com.damaba.damaba.application.port.inbound.photographer.ExistsPhotographerNicknameUseCase
import com.damaba.damaba.application.port.inbound.photographer.FindPhotographerListUseCase
import com.damaba.damaba.application.port.inbound.photographer.FindSavedPhotographerListUseCase
import com.damaba.damaba.application.port.inbound.photographer.GetPhotographerUseCase
import com.damaba.damaba.application.port.inbound.photographer.RegisterPhotographerUseCase
import com.damaba.damaba.application.port.inbound.photographer.SavePhotographerUseCase
import com.damaba.damaba.application.port.inbound.photographer.UnsavePhotographerUseCase
import com.damaba.damaba.application.port.inbound.photographer.UpdatePhotographerPageUseCase
import com.damaba.damaba.application.port.inbound.photographer.UpdatePhotographerProfileUseCase
import com.damaba.damaba.domain.common.Pagination
import com.damaba.damaba.domain.photographer.Photographer
import com.damaba.damaba.domain.photographer.PhotographerListItem
import com.damaba.damaba.domain.photographer.PhotographerSave
import com.damaba.damaba.domain.photographer.exception.AlreadyPhotographerSaveException
import com.damaba.damaba.domain.photographer.exception.PhotographerSaveNotFoundException
import com.damaba.damaba.domain.user.User
import com.damaba.damaba.domain.user.exception.NicknameAlreadyExistsException
import com.damaba.damaba.domain.user.exception.UserAlreadyRegisteredException
import com.damaba.damaba.infrastructure.photographer.PhotographerRepository
import com.damaba.damaba.infrastructure.photographer.PhotographerSaveRepository
import com.damaba.damaba.infrastructure.user.UserRepository
import com.damaba.damaba.mapper.PhotographerMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PhotographerService(
    private val userRepo: UserRepository,
    private val photographerRepo: PhotographerRepository,
    private val photographerSaveRepo: PhotographerSaveRepository,
) : GetPhotographerUseCase,
    FindPhotographerListUseCase,
    FindSavedPhotographerListUseCase,
    ExistsPhotographerNicknameUseCase,
    RegisterPhotographerUseCase,
    UpdatePhotographerProfileUseCase,
    UpdatePhotographerPageUseCase,
    SavePhotographerUseCase,
    UnsavePhotographerUseCase {

    @Transactional(readOnly = true)
    override fun getPhotographer(id: Long): Photographer = photographerRepo.getById(id)

    @Transactional(readOnly = true)
    override fun findPhotographerList(
        query: FindPhotographerListUseCase.Query,
    ): Pagination<PhotographerListItem> = photographerRepo.findPhotographerList(
        requestUserId = query.requestUserId,
        regions = query.regions,
        photographyTypes = query.photographyTypes,
        sort = query.sort,
        page = query.page,
        pageSize = query.pageSize,
    )

    @Transactional(readOnly = true)
    override fun findSavedPhotographerList(
        query: FindSavedPhotographerListUseCase.Query,
    ): Pagination<PhotographerListItem> = photographerRepo.findSavedPhotographerList(
        requestUserId = query.requestUserId,
        page = query.page,
        pageSize = query.pageSize,
    )

    @Transactional(readOnly = true)
    override fun existsNickname(query: ExistsPhotographerNicknameUseCase.Query): Boolean = userRepo.existsNickname(query.nickname)

    @Transactional
    override fun register(command: RegisterPhotographerUseCase.Command): Photographer {
        val user: User = userRepo.getById(command.userId)
        if (user.isRegistrationCompleted) {
            throw UserAlreadyRegisteredException()
        }

        if (userRepo.existsNickname(command.nickname)) {
            throw NicknameAlreadyExistsException(command.nickname)
        }

        val photographer = Photographer.create(user)
        photographer.registerPhotographer(
            nickname = command.nickname,
            gender = command.gender,
            instagramId = command.instagramId,
            profileImage = command.profileImage,
            mainPhotographyTypes = command.mainPhotographyTypes,
            activeRegions = command.activeRegions,
        )
        return photographerRepo.createIfUserExists(photographer)
    }

    @Transactional
    override fun updatePhotographerProfile(command: UpdatePhotographerProfileUseCase.Command): Photographer {
        val photographer = photographerRepo.getById(command.photographerId)

        if (photographer.nickname != command.nickname && userRepo.existsNickname(command.nickname)) {
            throw NicknameAlreadyExistsException(command.nickname)
        }
        photographer.profileImage?.let { originalProfileImage ->
            if (originalProfileImage != command.profileImage) {
                userRepo.deleteByUrl(originalProfileImage.url)
            }
        }

        photographer.updateProfile(PhotographerMapper.INSTANCE.toPhotographerProfile(command))
        return photographerRepo.update(photographer)
    }

    @Transactional
    override fun updatePhotographerPage(command: UpdatePhotographerPageUseCase.Command): Photographer {
        val photographer = photographerRepo.getById(command.photographerId)
        photographer.updatePage(PhotographerMapper.INSTANCE.toPhotographerPage(command))
        return photographerRepo.update(photographer)
    }

    @Transactional
    override fun savePhotographer(command: SavePhotographerUseCase.Command) {
        if (photographerSaveRepo.existsByUserIdAndPhotographerId(command.requestUserId, command.photographerId)) {
            throw AlreadyPhotographerSaveException()
        }
        photographerSaveRepo.create(
            PhotographerSave.create(userId = command.requestUserId, photographerId = command.photographerId),
        )
    }

    @Transactional
    override fun unsavePhotographer(command: UnsavePhotographerUseCase.Command) {
        val photographerSave =
            photographerSaveRepo.findByUserIdAndPhotographerId(command.requestUserId, command.photographerId)
                ?: throw PhotographerSaveNotFoundException()
        photographerSaveRepo.delete(photographerSave)
    }
}
