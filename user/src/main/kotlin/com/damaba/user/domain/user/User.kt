package com.damaba.user.domain.user

import com.damaba.user.domain.user.constant.Gender
import com.damaba.user.domain.user.constant.LoginType
import com.damaba.user.domain.user.constant.UserRoleType
import java.time.LocalDate

class User(
    val id: Long,
    val roles: Set<UserRoleType>,
    val loginType: LoginType,
    val oAuthLoginUid: String,
    nickname: String,
    profileImageUrl: String,
    gender: Gender,
    birthDate: LocalDate,
    instagramId: String?,
) {
    var nickname: String = nickname
        private set

    var profileImageUrl: String = profileImageUrl
        private set

    var gender: Gender = gender
        private set

    var birthDate: LocalDate = birthDate
        private set

    var instagramId: String? = instagramId
        private set

    val isRegistrationCompleted
        get() = birthDate != DEFAULT_BIRTH_DATE

    fun update(
        nickname: String?,
        gender: Gender?,
        birthDate: LocalDate?,
        instagramId: String?,
        profileImageUrl: String?,
    ) {
        nickname?.let { this.nickname = it }
        gender?.let { this.gender = it }
        birthDate?.let { this.birthDate = it }
        instagramId?.let { this.instagramId = it }
        profileImageUrl?.let { this.profileImageUrl = it }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is User) return false
        return this.id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

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
}
