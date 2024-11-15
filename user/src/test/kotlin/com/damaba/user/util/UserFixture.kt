package com.damaba.user.util

import com.damaba.common_file.domain.Image
import com.damaba.user.domain.user.User
import com.damaba.user.domain.user.constant.Gender
import com.damaba.user.domain.user.constant.LoginType
import com.damaba.user.domain.user.constant.UserRoleType
import com.damaba.user.domain.user.constant.UserType
import com.damaba.user.util.RandomTestUtils.Companion.randomLong
import com.damaba.user.util.RandomTestUtils.Companion.randomString
import com.damaba.user.util.RandomTestUtils.Companion.randomUrl

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
}
