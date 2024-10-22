package com.damaba.user.application.user

import com.damaba.user.domain.user.UserService
import com.damaba.user.domain.user.UserValidator
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CheckNicknameExistenceUseCase(private val userService: UserService) {
    @Transactional(readOnly = true)
    operator fun invoke(command: Command): Boolean =
        !userService.doesNicknameExist(command.nickname)

    data class Command(
        val nickname: String,
    ) {
        init {
            UserValidator.validateNickname(nickname)
        }
    }
}
