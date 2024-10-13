package com.damaba.user.application.user

import com.damaba.user.domain.user.UserService
import org.springframework.stereotype.Service

@Service
class CheckNicknameAvailabilityUseCase(private val userService: UserService) {
    operator fun invoke(nickname: String): Boolean =
        !userService.doesNicknameExist(nickname)
}
