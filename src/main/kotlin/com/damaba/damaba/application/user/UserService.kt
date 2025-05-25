package com.damaba.damaba.application.user

import com.damaba.damaba.application.user.dto.ExistsUserNicknameQuery
import com.damaba.damaba.application.user.dto.RegisterUserCommand
import com.damaba.damaba.application.user.dto.UpdateUserProfileCommand
import com.damaba.damaba.domain.user.User
import com.damaba.damaba.domain.user.exception.NicknameAlreadyExistsException
import com.damaba.damaba.domain.user.exception.UserAlreadyRegisteredException
import com.damaba.damaba.domain.user.exception.UserNotFoundException
import com.damaba.damaba.infrastructure.user.UserRepository
import com.damaba.damaba.mapper.UserMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(private val userRepo: UserRepository) {

    @Transactional(readOnly = true)
    fun getUser(userId: Long): User = userRepo.getById(userId)

    @Transactional(readOnly = true)
    fun existsNickname(query: ExistsUserNicknameQuery): Boolean = userRepo.existsNickname(query.nickname)

    /**
     * 유저를 등록한다. 즉, 유저 등록 정보를 수정한다.
     * '유저 등록 정보'란 회원가입 시 유저에게 입력받는 정보를 의미한다.
     *
     * @param command
     * @return 등록된 유저
     * @throws UserNotFoundException `userId`에 해당하는 유저를 찾을 수 없는 경우
     * @throws UserAlreadyRegisteredException 이미 등록된 유저인 경우
     * @throws NicknameAlreadyExistsException `nickname`을 다른 유저가 이미 사용중인 경우
     */
    @Transactional
    fun register(command: RegisterUserCommand): User {
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
    fun updateUserProfile(command: UpdateUserProfileCommand): User {
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
