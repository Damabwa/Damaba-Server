package com.damaba.damaba.application.service.photographer

import com.damaba.damaba.application.port.inbound.photographer.CheckPhotographerNicknameExistenceUseCase
import com.damaba.damaba.application.port.inbound.photographer.GetPhotographerUseCase
import com.damaba.damaba.application.port.inbound.photographer.RegisterPhotographerUseCase
import com.damaba.damaba.application.port.outbound.photographer.CreatePhotographerPort
import com.damaba.damaba.application.port.outbound.photographer.GetPhotographerPort
import com.damaba.damaba.application.port.outbound.user.CheckNicknameExistencePort
import com.damaba.damaba.application.port.outbound.user.GetUserPort
import com.damaba.damaba.domain.photographer.Photographer
import com.damaba.damaba.domain.user.User
import com.damaba.damaba.domain.user.exception.NicknameAlreadyExistsException
import com.damaba.damaba.domain.user.exception.UserAlreadyRegisteredException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PhotographerService(
    private val getUserPort: GetUserPort,
    private val getPhotographerPort: GetPhotographerPort,
    private val checkNicknameExistencePort: CheckNicknameExistencePort,
    private val createPhotographerPort: CreatePhotographerPort,
) : GetPhotographerUseCase,
    CheckPhotographerNicknameExistenceUseCase,
    RegisterPhotographerUseCase {

    @Transactional(readOnly = true)
    override fun getPhotographer(id: Long): Photographer = getPhotographerPort.getById(id)

    @Transactional(readOnly = true)
    override fun doesNicknameExist(query: CheckPhotographerNicknameExistenceUseCase.Query): Boolean = checkNicknameExistencePort.doesNicknameExist(query.nickname)

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
        return createPhotographerPort.createIfUserExists(photographer)
    }
}
