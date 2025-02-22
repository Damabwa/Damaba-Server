package com.damaba.damaba.application.service.user

import com.damaba.damaba.application.port.inbound.user.ExistsUserNicknameUseCase
import com.damaba.damaba.application.port.inbound.user.GetUserUseCase
import com.damaba.damaba.application.port.inbound.user.RegisterUserUseCase
import com.damaba.damaba.application.port.inbound.user.UpdateUserProfileUseCase
import com.damaba.damaba.application.port.outbound.user.DeleteUserProfileImagePort
import com.damaba.damaba.application.port.outbound.user.ExistsNicknamePort
import com.damaba.damaba.application.port.outbound.user.GetUserPort
import com.damaba.damaba.application.port.outbound.user.UpdateUserPort
import com.damaba.damaba.domain.user.User
import com.damaba.damaba.domain.user.exception.NicknameAlreadyExistsException
import com.damaba.damaba.domain.user.exception.UserAlreadyRegisteredException
import com.damaba.damaba.mapper.UserMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val getUserPort: GetUserPort,
    private val existsNicknamePort: ExistsNicknamePort,
    private val updateUserPort: UpdateUserPort,
    private val deleteUserProfileImagePort: DeleteUserProfileImagePort,
) : GetUserUseCase,
    ExistsUserNicknameUseCase,
    UpdateUserProfileUseCase,
    RegisterUserUseCase {

    @Transactional(readOnly = true)
    override fun getUser(userId: Long): User = getUserPort.getById(userId)

    @Transactional(readOnly = true)
    override fun existsNickname(query: ExistsUserNicknameUseCase.Query): Boolean = existsNicknamePort.existsNickname(query.nickname)

    @Transactional
    override fun register(command: RegisterUserUseCase.Command): User {
        val user = getUserPort.getById(command.userId)

        if (user.isRegistrationCompleted) {
            throw UserAlreadyRegisteredException()
        }

        if (existsNicknamePort.existsNickname(command.nickname)) {
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
    override fun updateUserProfile(command: UpdateUserProfileUseCase.Command): User {
        val user = getUserPort.getById(command.userId)

        if ((user.nickname != command.nickname) && existsNicknamePort.existsNickname(command.nickname)) {
            throw NicknameAlreadyExistsException(command.nickname)
        }
        if (user.profileImage != command.profileImage) {
            deleteUserProfileImagePort.deleteProfileImageIfExists(user.profileImage.url)
        }

        user.updateProfile(UserMapper.INSTANCE.toUserProfile(command))
        return updateUserPort.update(user)
    }
}
