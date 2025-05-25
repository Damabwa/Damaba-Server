package com.damaba.damaba.application.photographer

import com.damaba.damaba.application.photographer.dto.ExistsPhotographerNicknameQuery
import com.damaba.damaba.application.photographer.dto.FindPhotographerListQuery
import com.damaba.damaba.application.photographer.dto.FindSavedPhotographerListQuery
import com.damaba.damaba.application.photographer.dto.RegisterPhotographerCommand
import com.damaba.damaba.application.photographer.dto.SavePhotographerCommand
import com.damaba.damaba.application.photographer.dto.UnsavePhotographerCommand
import com.damaba.damaba.application.photographer.dto.UpdatePhotographerPageCommand
import com.damaba.damaba.application.photographer.dto.UpdatePhotographerProfileCommand
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
) {
    @Transactional
    fun register(command: RegisterPhotographerCommand): Photographer {
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
    fun savePhotographer(command: SavePhotographerCommand) {
        if (photographerSaveRepo.existsByUserIdAndPhotographerId(command.requestUserId, command.photographerId)) {
            throw AlreadyPhotographerSaveException()
        }
        photographerSaveRepo.create(
            PhotographerSave.create(userId = command.requestUserId, photographerId = command.photographerId),
        )
    }

    @Transactional(readOnly = true)
    fun getPhotographer(id: Long): Photographer = photographerRepo.getById(id)

    @Transactional(readOnly = true)
    fun findPhotographerList(
        query: FindPhotographerListQuery,
    ): Pagination<PhotographerListItem> = photographerRepo.findPhotographerList(
        requestUserId = query.requestUserId,
        regions = query.regions,
        photographyTypes = query.photographyTypes,
        sort = query.sort,
        page = query.page,
        pageSize = query.pageSize,
    )

    @Transactional(readOnly = true)
    fun findSavedPhotographerList(
        query: FindSavedPhotographerListQuery,
    ): Pagination<PhotographerListItem> = photographerRepo.findSavedPhotographerList(
        requestUserId = query.requestUserId,
        page = query.page,
        pageSize = query.pageSize,
    )

    @Transactional(readOnly = true)
    fun existsNickname(query: ExistsPhotographerNicknameQuery): Boolean = userRepo.existsNickname(query.nickname)

    @Transactional
    fun updatePhotographerProfile(command: UpdatePhotographerProfileCommand): Photographer {
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
    fun updatePhotographerPage(command: UpdatePhotographerPageCommand): Photographer {
        val photographer = photographerRepo.getById(command.photographerId)
        photographer.updatePage(PhotographerMapper.INSTANCE.toPhotographerPage(command))
        return photographerRepo.update(photographer)
    }

    @Transactional
    fun unsavePhotographer(command: UnsavePhotographerCommand) {
        val photographerSave =
            photographerSaveRepo.findByUserIdAndPhotographerId(command.requestUserId, command.photographerId)
                ?: throw PhotographerSaveNotFoundException()
        photographerSaveRepo.delete(photographerSave)
    }
}
