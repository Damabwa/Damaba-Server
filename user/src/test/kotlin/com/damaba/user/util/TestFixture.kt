package com.damaba.user.util

import com.damaba.user.domain.auth.AuthToken
import com.damaba.user.domain.user.User
import com.damaba.user.domain.user.constant.Gender
import com.damaba.user.domain.user.constant.LoginType
import com.damaba.user.util.RandomTestUtils.Companion.randomInt
import com.damaba.user.util.RandomTestUtils.Companion.randomLong
import com.damaba.user.util.RandomTestUtils.Companion.randomString
import java.time.LocalDateTime

object TestFixture {
    fun createUser(
        id: Long = randomLong(),
        oAuthLoginUid: String = randomString(),
        loginType: LoginType = LoginType.KAKAO,
        nickname: String = randomString(),
        gender: Gender = Gender.MALE,
        age: Int = randomInt(),
        instagramId: String = randomString(),
    ): User = User(
        id = id,
        oAuthLoginUid = oAuthLoginUid,
        loginType = loginType,
        nickname = nickname,
        gender = gender,
        age = age,
        instagramId = instagramId,
    )

    fun createAuthToken(
        value: String = randomString(),
        expiresAt: LocalDateTime = LocalDateTime.now(),
    ): AuthToken = AuthToken(
        value = value,
        expiresAt = expiresAt,
    )
}
