package com.damaba.user.application.port.inbound.user

import com.damaba.user.domain.user.User

interface GetUserUseCase {
    fun getUser(userId: Long): User
}
