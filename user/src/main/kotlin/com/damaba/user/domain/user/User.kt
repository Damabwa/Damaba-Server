package com.damaba.user.domain.user

import com.damaba.user.domain.user.constant.Gender
import com.damaba.user.domain.user.constant.LoginType
import com.damaba.user.domain.user.constant.UserRoleType
import java.time.LocalDate

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
        val DEFAULT_BIRTH_DATE: LocalDate = LocalDate.of(1, 1, 1)
        const val DEFAULT_PROFILE_IMAGE_URL = "https://dummyimage.com/244x100.png/cc0000/ffffff"

        fun create(loginType: LoginType, oAuthLoginUid: String, nickname: String): User = User(
            id = 0,
            roles = setOf(UserRoleType.USER),
            loginType = loginType,
            oAuthLoginUid = oAuthLoginUid,
            nickname = nickname,
            profileImageUrl = DEFAULT_PROFILE_IMAGE_URL,
            gender = Gender.PRIVATE,
            birthDate = DEFAULT_BIRTH_DATE,
            instagramId = null,
        )
    }

    val isRegistrationCompleted
        get() = birthDate != DEFAULT_BIRTH_DATE

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
