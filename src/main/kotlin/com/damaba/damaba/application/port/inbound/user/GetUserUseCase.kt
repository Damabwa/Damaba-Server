package com.damaba.damaba.application.port.inbound.user

import com.damaba.damaba.domain.user.User

interface GetUserUseCase {
    fun getUser(userId: Long): User
}
