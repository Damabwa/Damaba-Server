package com.damaba.user.domain.user

import com.damaba.user.domain.user.constant.Gender
import com.damaba.user.domain.user.constant.LoginType
import com.damaba.user.domain.user.constant.UserRoleType
import java.util.UUID

data class User(
    val loginType: LoginType,
    val oAuthLoginUid: String,

    val id: Long = 0,
    val roles: Set<UserRoleType> = setOf(UserRoleType.ROLE_USER),
    val nickname: String = UUID.randomUUID().toString(),
    val gender: Gender = Gender.PRIVATE,
    val age: Int = -1,
    val instagramId: String? = null,
) {
    val isRegistrationCompleted
        get() = (age == -1)
}
