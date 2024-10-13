package com.damaba.user.application.user

import com.damaba.user.domain.user.User
import com.damaba.user.domain.user.UserService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetMyInfoUseCase(private val userService: UserService) {
    @Transactional(readOnly = true)
    operator fun invoke(userId: Long): User =
        userService.getUserById(userId)
}
