package com.damaba.user.domain.user

import com.damaba.user.domain.user.constant.Gender
import com.damaba.user.domain.user.constant.LoginType
import com.damaba.user.domain.user.constant.UserRoleType
import java.time.LocalDate
import java.util.UUID

data class User(
    val id: Long,
    val roles: Set<UserRoleType>,
    val loginType: LoginType,
    val oAuthLoginUid: String,
    val nickname: String,
    val profileImageUrl: String,
    val gender: Gender,
    val birthDate: LocalDate,
    val instagramId: String?,
) {
    companion object {
        const val DEFAULT_PROFILE_IMAGE_URL = "https://dummyimage.com/244x100.png/cc0000/ffffff"

        fun create(loginType: LoginType, oAuthLoginUid: String): User = User(
            id = 0,
            roles = setOf(UserRoleType.USER),
            loginType = loginType,
            oAuthLoginUid = oAuthLoginUid,
            nickname = UUID.randomUUID().toString(),
            profileImageUrl = DEFAULT_PROFILE_IMAGE_URL,
            gender = Gender.PRIVATE,
            birthDate = LocalDate.MIN,
            instagramId = null,
        )
    }

    val isRegistrationCompleted
        get() = birthDate != LocalDate.MIN

    fun update(
        nickname: String?,
        gender: Gender?,
        birthDate: LocalDate?,
        instagramId: String?,
        profileImageUrl: String?,
    ): User = this.copy(
        nickname = nickname ?: this.nickname,
        gender = gender ?: this.gender,
        birthDate = birthDate ?: this.birthDate,
        instagramId = instagramId ?: this.instagramId,
        profileImageUrl = profileImageUrl ?: this.profileImageUrl,
    )
}
