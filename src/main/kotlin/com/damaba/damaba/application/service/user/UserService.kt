package com.damaba.damaba.application.service.user

import com.damaba.damaba.application.port.inbound.user.CheckUserNicknameExistenceUseCase
import com.damaba.damaba.application.port.inbound.user.UpdateUserUseCase
import com.damaba.damaba.domain.file.DeleteFileEvent
import com.damaba.damaba.domain.user.User
import com.damaba.user.application.port.inbound.user.GetUserUseCase
import com.damaba.user.application.port.inbound.user.RegisterUserUseCase
import com.damaba.user.application.port.outbound.common.PublishEventPort
import com.damaba.user.application.port.outbound.user.CheckNicknameExistencePort
import com.damaba.user.application.port.outbound.user.GetUserPort
import com.damaba.user.application.port.outbound.user.UpdateUserPort
import com.damaba.user.domain.user.exception.NicknameAlreadyExistsException
import com.damaba.user.domain.user.exception.UserAlreadyRegisteredException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val getUserPort: GetUserPort,
    private val checkNicknameExistencePort: CheckNicknameExistencePort,
    private val updateUserPort: UpdateUserPort,
    private val publishEventPort: PublishEventPort,
) : GetUserUseCase,
    CheckUserNicknameExistenceUseCase,
    UpdateUserUseCase,
    RegisterUserUseCase {

    @Transactional(readOnly = true)
    override fun getUser(userId: Long): User = getUserPort.getById(userId)

    @Transactional(readOnly = true)
    override fun doesNicknameExist(query: CheckUserNicknameExistenceUseCase.Query): Boolean = checkNicknameExistencePort.doesNicknameExist(query.nickname)

    @Transactional
    override fun register(command: RegisterUserUseCase.Command): User {
        val user = getUserPort.getById(command.userId)

        if (user.isRegistrationCompleted) {
            throw UserAlreadyRegisteredException()
        }

        if (checkNicknameExistencePort.doesNicknameExist(command.nickname)) {
            throw NicknameAlreadyExistsException(command.nickname)
        }
        user.registerUser(
            nickname = command.nickname,
            gender = command.gender,
            instagramId = command.instagramId,
        )
        return updateUserPort.update(user)
    }

    @Transactional
    override fun updateUser(command: UpdateUserUseCase.Command): User {
        val user = getUserPort.getById(command.userId)

        val isNicknameNew = user.nickname != command.nickname
        if (isNicknameNew && checkNicknameExistencePort.doesNicknameExist(command.nickname)) {
            throw NicknameAlreadyExistsException(command.nickname)
        }

        if (user.profileImage != command.profileImage) {
            publishEventPort.publish(DeleteFileEvent(url = user.profileImage.url))
        }

        user.update(
            nickname = command.nickname,
            instagramId = command.instagramId,
            profileImage = command.profileImage,
        )
        return updateUserPort.update(user)
    }
}
