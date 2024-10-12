package com.damaba.user.domain.user

import com.damaba.user.domain.user.constant.Gender
import com.damaba.user.domain.user.constant.LoginType
import com.damaba.user.domain.user.constant.UserRoleType
import java.time.LocalDateTime
import java.util.UUID

data class User(
    val id: Long = 0,
    val roles: Set<UserRoleType> = setOf(UserRoleType.ROLE_USER),
    val loginType: LoginType,
    val oAuthLoginUid: String,
    val nickname: String = UUID.randomUUID().toString(),
    val gender: Gender = Gender.PRIVATE,
    val age: Int = -1,
    val instagramId: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.MIN,
    val updatedAt: LocalDateTime = LocalDateTime.MIN,
) {
    val isRegistrationCompleted
        get() = (this.createdAt != this.updatedAt) && (age == -1)
}
