package com.damaba.damaba.application.user

import com.damaba.damaba.application.port.inbound.user.ExistsUserNicknameUseCase
import com.damaba.damaba.application.port.inbound.user.GetUserUseCase
import com.damaba.damaba.application.port.inbound.user.RegisterUserUseCase
import com.damaba.damaba.application.port.inbound.user.UpdateUserProfileUseCase
import com.damaba.damaba.domain.user.User
import com.damaba.damaba.domain.user.exception.NicknameAlreadyExistsException
import com.damaba.damaba.domain.user.exception.UserAlreadyRegisteredException
import com.damaba.damaba.infrastructure.user.UserRepository
import com.damaba.damaba.mapper.UserMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(private val userRepo: UserRepository) :
    GetUserUseCase,
    ExistsUserNicknameUseCase,
    UpdateUserProfileUseCase,
    RegisterUserUseCase {

    @Transactional(readOnly = true)
    override fun getUser(userId: Long): User = userRepo.getById(userId)

    @Transactional(readOnly = true)
    override fun existsNickname(query: ExistsUserNicknameUseCase.Query): Boolean = userRepo.existsNickname(query.nickname)

    @Transactional
    override fun register(command: RegisterUserUseCase.Command): User {
        val user = userRepo.getById(command.userId)

        if (user.isRegistrationCompleted) {
            throw UserAlreadyRegisteredException()
        }

        if (userRepo.existsNickname(command.nickname)) {
            throw NicknameAlreadyExistsException(command.nickname)
        }
        user.registerUser(
            nickname = command.nickname,
            gender = command.gender,
            instagramId = command.instagramId,
        )
        return userRepo.update(user)
    }

    @Transactional
    override fun updateUserProfile(command: UpdateUserProfileUseCase.Command): User {
        val user = userRepo.getById(command.userId)

        if ((user.nickname != command.nickname) && userRepo.existsNickname(command.nickname)) {
            throw NicknameAlreadyExistsException(command.nickname)
        }
        user.profileImage?.let { originalProfileImage ->
            if (originalProfileImage != command.profileImage) {
                userRepo.deleteByUrl(originalProfileImage.url)
            }
        }

        user.updateProfile(UserMapper.INSTANCE.toUserProfile(command))
        return userRepo.update(user)
    }
}
