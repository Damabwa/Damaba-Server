package com.damaba.user.domain.user

import com.damaba.user.domain.user.constant.Gender
import com.damaba.user.domain.user.constant.LoginType
import com.damaba.user.domain.user.constant.UserRoleType
import java.util.UUID

data class User(
    val loginType: LoginType,
    val oAuthLoginUid: String,

    val id: Long = 0,
    val roles: Set<UserRoleType> = setOf(UserRoleType.USER),
    val nickname: String = UUID.randomUUID().toString(),
    val profileImageUrl: String = DEFAULT_PROFILE_IMAGE_URL,
    val gender: Gender = Gender.PRIVATE,
    val age: Int = -1,
    val instagramId: String? = null,
) {
    companion object {
        private const val DEFAULT_PROFILE_IMAGE_URL = "https://dummyimage.com/244x100.png/cc0000/ffffff"
    }

    val isRegistrationCompleted
        get() = age == -1

    fun update(
        nickname: String?,
        gender: Gender?,
        age: Int?,
        instagramId: String?,
    ): User = this.copy(
        nickname = nickname ?: this.nickname,
        gender = gender ?: this.gender,
        age = age ?: this.age,
        instagramId = instagramId ?: this.instagramId,
    )
}
