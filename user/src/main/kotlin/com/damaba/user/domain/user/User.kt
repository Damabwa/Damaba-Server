package com.damaba.user.domain.user

import com.damaba.user.domain.user.constant.Gender
import com.damaba.user.domain.user.constant.LoginType
import com.damaba.user.domain.user.constant.UserRoleType
import com.damaba.user.domain.user.constant.UserType
import java.util.Objects

class User(
    val id: Long,
    val loginType: LoginType,
    val oAuthLoginUid: String,
    type: UserType,
    roles: Set<UserRoleType>,
    nickname: String,
    profileImage: UserProfileImage,
    gender: Gender,
    instagramId: String?,
) {
    var type: UserType = type
        private set

    var roles: Set<UserRoleType> = roles
        private set

    var nickname: String = nickname
        private set

    var profileImage: UserProfileImage = profileImage
        private set

    var gender: Gender = gender
        private set

    var instagramId: String? = instagramId
        private set

    val isRegistrationCompleted
        get() = type != UserType.UNDEFINED

    fun register(
        type: UserType,
        nickname: String,
        gender: Gender,
        instagramId: String?,
    ) {
        this.type = type
        // TODO: type이 사진작가인 경우 roles 수정
        this.nickname = nickname
        this.gender = gender
        this.instagramId = instagramId
    }

    fun update(
        nickname: String,
        instagramId: String?,
        profileImage: UserProfileImage,
    ) {
        this.nickname = nickname
        this.instagramId = instagramId
        this.profileImage = profileImage
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is User) return false
        return this.id == other.id
    }

    override fun hashCode(): Int = Objects.hashCode(id)

    companion object {
        val DEFAULT_PROFILE_IMAGE = UserProfileImage(
            name = "default-user-profile-image.jpg",
            url = "https://dummyimage.com/244x100.png/cc0000/ffffff",
        )

        fun create(loginType: LoginType, oAuthLoginUid: String, nickname: String): User = User(
            id = 0,
            type = UserType.UNDEFINED,
            roles = setOf(UserRoleType.USER),
            loginType = loginType,
            oAuthLoginUid = oAuthLoginUid,
            nickname = nickname,
            profileImage = DEFAULT_PROFILE_IMAGE,
            gender = Gender.MALE,
            instagramId = null,
        )
    }
}
