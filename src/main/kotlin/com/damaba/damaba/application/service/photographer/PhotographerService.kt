package com.damaba.damaba.application.service.photographer

import com.damaba.damaba.application.port.inbound.photographer.ExistsPhotographerNicknameUseCase
import com.damaba.damaba.application.port.inbound.photographer.FindPhotographerListUseCase
import com.damaba.damaba.application.port.inbound.photographer.GetPhotographerUseCase
import com.damaba.damaba.application.port.inbound.photographer.RegisterPhotographerUseCase
import com.damaba.damaba.application.port.inbound.photographer.SavePhotographerUseCase
import com.damaba.damaba.application.port.inbound.photographer.UnsavePhotographerUseCase
import com.damaba.damaba.application.port.inbound.photographer.UpdatePhotographerPageUseCase
import com.damaba.damaba.application.port.inbound.photographer.UpdatePhotographerProfileUseCase
import com.damaba.damaba.application.port.outbound.photographer.CreatePhotographerPort
import com.damaba.damaba.application.port.outbound.photographer.CreateSavedPhotographerPort
import com.damaba.damaba.application.port.outbound.photographer.DeleteSavedPhotographerPort
import com.damaba.damaba.application.port.outbound.photographer.ExistsSavedPhotographerPort
import com.damaba.damaba.application.port.outbound.photographer.FindPhotographerListPort
import com.damaba.damaba.application.port.outbound.photographer.FindSavedPhotographerPort
import com.damaba.damaba.application.port.outbound.photographer.GetPhotographerPort
import com.damaba.damaba.application.port.outbound.photographer.UpdatePhotographerPort
import com.damaba.damaba.application.port.outbound.user.DeleteUserProfileImagePort
import com.damaba.damaba.application.port.outbound.user.ExistsNicknamePort
import com.damaba.damaba.application.port.outbound.user.GetUserPort
import com.damaba.damaba.domain.common.Pagination
import com.damaba.damaba.domain.photographer.Photographer
import com.damaba.damaba.domain.photographer.PhotographerListItem
import com.damaba.damaba.domain.photographer.SavedPhotographer
import com.damaba.damaba.domain.photographer.exception.AlreadySavedPhotographerException
import com.damaba.damaba.domain.photographer.exception.SavedPhotographerNotFoundException
import com.damaba.damaba.domain.user.User
import com.damaba.damaba.domain.user.exception.NicknameAlreadyExistsException
import com.damaba.damaba.domain.user.exception.UserAlreadyRegisteredException
import com.damaba.damaba.mapper.PhotographerMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PhotographerService(
    private val getUserPort: GetUserPort,

    private val getPhotographerPort: GetPhotographerPort,
    private val findPhotographerListPort: FindPhotographerListPort,
    private val existsNicknamePort: ExistsNicknamePort,
    private val createPhotographerPort: CreatePhotographerPort,
    private val updatePhotographerPort: UpdatePhotographerPort,
    private val deleteUserProfileImagePort: DeleteUserProfileImagePort,

    private val findSavedPhotographerPort: FindSavedPhotographerPort,
    private val existsSavedPhotographerPort: ExistsSavedPhotographerPort,
    private val createSavedPhotographerPort: CreateSavedPhotographerPort,
    private val deleteSavedPhotographerPort: DeleteSavedPhotographerPort,
) : GetPhotographerUseCase,
    FindPhotographerListUseCase,
    ExistsPhotographerNicknameUseCase,
    RegisterPhotographerUseCase,
    UpdatePhotographerProfileUseCase,
    UpdatePhotographerPageUseCase,
    SavePhotographerUseCase,
    UnsavePhotographerUseCase {

    @Transactional(readOnly = true)
    override fun getPhotographer(id: Long): Photographer = getPhotographerPort.getById(id)

    @Transactional(readOnly = true)
    override fun findPhotographerList(
        query: FindPhotographerListUseCase.Query,
    ): Pagination<PhotographerListItem> = findPhotographerListPort.find(
        reqUserId = query.reqUserId,
        regions = query.regions,
        photographyTypes = query.photographyTypes,
        sort = query.sort,
        page = query.page,
        pageSize = query.pageSize,
    )

    @Transactional(readOnly = true)
    override fun existsNickname(query: ExistsPhotographerNicknameUseCase.Query): Boolean = existsNicknamePort.existsNickname(query.nickname)

    @Transactional
    override fun register(command: RegisterPhotographerUseCase.Command): Photographer {
        val user: User = getUserPort.getById(command.userId)
        if (user.isRegistrationCompleted) {
            throw UserAlreadyRegisteredException()
        }

        if (existsNicknamePort.existsNickname(command.nickname)) {
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
        return createPhotographerPort.createIfUserExists(photographer)
    }

    @Transactional
    override fun updatePhotographerProfile(command: UpdatePhotographerProfileUseCase.Command): Photographer {
        val photographer = getPhotographerPort.getById(command.photographerId)

        if (photographer.nickname != command.nickname && existsNicknamePort.existsNickname(command.nickname)) {
            throw NicknameAlreadyExistsException(command.nickname)
        }
        if (photographer.profileImage != command.profileImage) {
            deleteUserProfileImagePort.deleteProfileImageIfExists(photographer.profileImage.url)
        }

        photographer.updateProfile(PhotographerMapper.INSTANCE.toPhotographerProfile(command))
        return updatePhotographerPort.update(photographer)
    }

    @Transactional
    override fun updatePhotographerPage(command: UpdatePhotographerPageUseCase.Command): Photographer {
        val photographer = getPhotographerPort.getById(command.photographerId)
        photographer.updatePage(PhotographerMapper.INSTANCE.toPhotographerPage(command))
        return updatePhotographerPort.update(photographer)
    }

    @Transactional
    override fun savePhotographer(command: SavePhotographerUseCase.Command) {
        if (existsSavedPhotographerPort.existsByUserIdAndPhotographerId(command.reqUserId, command.photographerId)) {
            throw AlreadySavedPhotographerException()
        }
        createSavedPhotographerPort.create(
            SavedPhotographer.create(userId = command.reqUserId, photographerId = command.photographerId),
        )
    }

    @Transactional
    override fun unsavePhotographer(command: UnsavePhotographerUseCase.Command) {
        val savedPhotographer =
            findSavedPhotographerPort.findByUserIdAndPhotographerId(command.reqUserId, command.photographerId)
                ?: throw SavedPhotographerNotFoundException()
        deleteSavedPhotographerPort.delete(savedPhotographer)
    }
}
