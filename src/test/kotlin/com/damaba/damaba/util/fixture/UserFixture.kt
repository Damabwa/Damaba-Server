package com.damaba.damaba.util.fixture

import com.damaba.damaba.adapter.outbound.user.UserJpaEntity
import com.damaba.damaba.adapter.outbound.user.UserProfileImageJpaEmbeddable
import com.damaba.damaba.domain.file.Image
import com.damaba.damaba.domain.user.User
import com.damaba.damaba.domain.user.UserProfile
import com.damaba.damaba.domain.user.constant.Gender
import com.damaba.damaba.domain.user.constant.LoginType
import com.damaba.damaba.domain.user.constant.UserRoleType
import com.damaba.damaba.domain.user.constant.UserType
import com.damaba.damaba.util.RandomTestUtils.Companion.randomLong
import com.damaba.damaba.util.RandomTestUtils.Companion.randomString
import com.damaba.damaba.util.RandomTestUtils.Companion.randomUrl

object UserFixture {
    fun createUser(
        id: Long = randomLong(),
        type: UserType = UserType.USER,
        roles: Set<UserRoleType> = setOf(UserRoleType.USER),
        oAuthLoginUid: String = randomString(),
        loginType: LoginType = LoginType.KAKAO,
        nickname: String = randomString(len = 7),
        profileImage: Image = Image(randomString(), randomUrl()),
        gender: Gender = Gender.MALE,
        instagramId: String? = randomString(len = 30),
    ): User = User(
        id = id,
        type = type,
        roles = roles,
        oAuthLoginUid = oAuthLoginUid,
        loginType = loginType,
        nickname = nickname,
        profileImage = profileImage,
        gender = gender,
        instagramId = instagramId,
    )

    fun createUserProfile(
        nickname: String = randomString(len = 7),
        instagramId: String = randomString(len = 10),
        profileImage: Image = FileFixture.createImage(),
    ) = UserProfile(
        nickname = nickname,
        instagramId = instagramId,
        profileImage = profileImage,
    )

    fun createUserJpaEntity(
        type: UserType = UserType.USER,
        roles: Set<UserRoleType> = setOf(UserRoleType.USER),
        oAuthLoginUid: String = randomString(),
        loginType: LoginType = LoginType.KAKAO,
        nickname: String = randomString(len = 7),
        profileImage: UserProfileImageJpaEmbeddable = UserProfileImageJpaEmbeddable(randomString(), randomUrl()),
        gender: Gender = Gender.MALE,
        instagramId: String = randomString(len = 30),
    ) = UserJpaEntity(
        type = type,
        roles = roles,
        oAuthLoginUid = oAuthLoginUid,
        loginType = loginType,
        nickname = nickname,
        profileImage = profileImage,
        gender = gender,
        instagramId = instagramId,
    )
}
