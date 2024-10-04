package com.damaba.user.domain.user

import com.damaba.user.domain.user.constant.LoginType
import com.damaba.user.domain.user.constant.UserRoleType
import java.time.LocalDateTime

data class User(
    val id: Long = 0,
    val roles: Set<UserRoleType> = setOf(UserRoleType.ROLE_USER),
    val oAuthLoginUid: String,
    val loginType: LoginType,
    val createdAt: LocalDateTime = LocalDateTime.MIN,
    val updatedAt: LocalDateTime = LocalDateTime.MIN,
    val deletedAt: LocalDateTime? = null,
)
