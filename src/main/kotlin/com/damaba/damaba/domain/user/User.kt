package com.damaba.damaba.domain.user

import com.damaba.damaba.domain.file.Image
import com.damaba.damaba.domain.user.constant.Gender
import com.damaba.damaba.domain.user.constant.LoginType
import com.damaba.damaba.domain.user.constant.UserRoleType
import com.damaba.damaba.domain.user.constant.UserType
import java.util.Objects

open class User(
    val id: Long,
    val loginType: LoginType,
    val oAuthLoginUid: String,
    type: UserType,
    roles: Set<UserRoleType>,
    nickname: String,
    profileImage: Image?,
    gender: Gender,
    instagramId: String?,
) {
    var type: UserType = type
        protected set

    var roles: Set<UserRoleType> = roles
        protected set

    var nickname: String = nickname
        protected set

    var profileImage: Image? = profileImage
        protected set

    var gender: Gender = gender
        protected set

    var instagramId: String? = instagramId
        protected set

    val isRegistrationCompleted
        get() = type != UserType.UNDEFINED

    fun registerUser(
        nickname: String,
        gender: Gender,
        instagramId: String?,
    ) {
        this.type = UserType.USER
        this.nickname = nickname
        this.gender = gender
        this.instagramId = instagramId
    }

    fun updateProfile(profile: UserProfile) {
        this.nickname = profile.nickname
        this.instagramId = profile.instagramId
        this.profileImage = profile.profileImage
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is User) return false
        return this.id == other.id
    }

    override fun hashCode(): Int = Objects.hashCode(id)

    companion object {
        fun create(loginType: LoginType, oAuthLoginUid: String, nickname: String): User = User(
            id = 0,
            type = UserType.UNDEFINED,
            roles = setOf(UserRoleType.USER),
            loginType = loginType,
            oAuthLoginUid = oAuthLoginUid,
            nickname = nickname,
            profileImage = null,
            gender = Gender.MALE,
            instagramId = null,
        )
    }
}
