package com.damaba.user.util

import com.damaba.user.domain.auth.AuthToken
import com.damaba.user.domain.file.UploadFile
import com.damaba.user.domain.user.User
import com.damaba.user.domain.user.constant.Gender
import com.damaba.user.domain.user.constant.LoginType
import com.damaba.user.domain.user.constant.UserRoleType
import com.damaba.user.util.RandomTestUtils.Companion.randomInt
import com.damaba.user.util.RandomTestUtils.Companion.randomLong
import com.damaba.user.util.RandomTestUtils.Companion.randomString
import java.time.LocalDateTime

object TestFixture {
    fun createUser(
        id: Long = randomLong(),
        roles: Set<UserRoleType> = setOf(UserRoleType.USER),
        oAuthLoginUid: String = randomString(),
        loginType: LoginType = LoginType.KAKAO,
        nickname: String = randomString(),
        profileImageUrl: String = randomString(),
        gender: Gender = Gender.MALE,
        age: Int = randomInt(),
        instagramId: String = randomString(),
    ): User = User(
        id = id,
        roles = roles,
        oAuthLoginUid = oAuthLoginUid,
        loginType = loginType,
        nickname = nickname,
        profileImageUrl = profileImageUrl,
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

    fun createUploadFile(
        name: String? = randomString(),
    ): UploadFile = UploadFile(
        name = name,
        size = randomLong(positive = true),
        contentType = "jpg",
        inputStream = randomString().byteInputStream(),
    )
}
