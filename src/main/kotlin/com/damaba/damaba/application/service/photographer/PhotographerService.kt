package com.damaba.damaba.application.service.photographer

import com.damaba.damaba.application.port.inbound.photographer.GetPhotographerUseCase
import com.damaba.damaba.application.port.inbound.photographer.RegisterPhotographerUseCase
import com.damaba.damaba.application.port.outbound.photographer.GetPhotographerPort
import com.damaba.damaba.application.port.outbound.photographer.SavePhotographerPort
import com.damaba.damaba.domain.photographer.Photographer
import com.damaba.damaba.domain.user.User
import com.damaba.user.application.port.outbound.user.CheckNicknameExistencePort
import com.damaba.user.application.port.outbound.user.GetUserPort
import com.damaba.user.domain.user.exception.NicknameAlreadyExistsException
import com.damaba.user.domain.user.exception.UserAlreadyRegisteredException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PhotographerService(
    private val getUserPort: GetUserPort,
    private val getPhotographerPort: GetPhotographerPort,
    private val checkNicknameExistencePort: CheckNicknameExistencePort,
    private val savePhotographerPort: SavePhotographerPort,
) : GetPhotographerUseCase,
    RegisterPhotographerUseCase {

    @Transactional(readOnly = true)
    override fun getById(id: Long): Photographer =
        getPhotographerPort.getById(id)

    @Transactional
    override fun register(command: RegisterPhotographerUseCase.Command): Photographer {
        val user: User = getUserPort.getById(command.userId)
        if (user.isRegistrationCompleted) {
            throw UserAlreadyRegisteredException()
        }

        if (checkNicknameExistencePort.doesNicknameExist(command.nickname)) {
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
        return savePhotographerPort.saveIfUserExists(photographer)
    }
}
