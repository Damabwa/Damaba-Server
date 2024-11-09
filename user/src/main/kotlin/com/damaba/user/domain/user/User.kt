package com.damaba.user.domain.user

import com.damaba.user.domain.user.constant.Gender
import com.damaba.user.domain.user.constant.LoginType
import com.damaba.user.domain.user.constant.UserRoleType
import java.util.Objects

class User(
    val id: Long,
    val roles: Set<UserRoleType>,
    val loginType: LoginType,
    val oAuthLoginUid: String,
    nickname: String,
    profileImageUrl: String,
    gender: Gender,
    instagramId: String?,
) {
    var nickname: String = nickname
        private set

    var profileImageUrl: String = profileImageUrl
        private set

    var gender: Gender = gender
        private set

    var instagramId: String? = instagramId
        private set

    val isRegistrationCompleted
        get() = gender != DEFAULT_GENDER

    fun update(
        nickname: String,
        gender: Gender,
        instagramId: String,
        profileImageUrl: String?,
    ) {
        this.nickname = nickname
        this.gender = gender
        this.instagramId = instagramId
        profileImageUrl?.let { this.profileImageUrl = it }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is User) return false
        return this.id == other.id
    }

    override fun hashCode(): Int = Objects.hashCode(id)

    companion object {
        val DEFAULT_GENDER = Gender.UNDEFINED
        const val DEFAULT_PROFILE_IMAGE_URL = "https://dummyimage.com/244x100.png/cc0000/ffffff"

        fun create(loginType: LoginType, oAuthLoginUid: String, nickname: String): User = User(
            id = 0,
            roles = setOf(UserRoleType.USER),
            loginType = loginType,
            oAuthLoginUid = oAuthLoginUid,
            nickname = nickname,
            profileImageUrl = DEFAULT_PROFILE_IMAGE_URL,
            gender = DEFAULT_GENDER,
            instagramId = null,
        )
    }
}
