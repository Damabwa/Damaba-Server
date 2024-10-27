package com.damaba.user.application.port.inbound.user

import com.damaba.user.domain.user.User

interface GetMyInfoUseCase {
    fun getMyInfo(userId: Long): User
}
