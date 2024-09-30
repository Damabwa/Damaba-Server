package com.damaba.user.domain.user

import com.damaba.user.domain.user.constant.LoginType
import java.time.LocalDateTime

data class User(
    val id: Long = 0,
    val oAuthLoginUid: String,
    val loginType: LoginType,
    val createdAt: LocalDateTime = LocalDateTime.MIN,
    val updatedAt: LocalDateTime = LocalDateTime.MIN,
    val deletedAt: LocalDateTime? = null,
)
