package com.damaba.user.util

import com.damaba.user.domain.auth.AuthToken
import com.damaba.user.domain.auth.AuthTokenType
import com.damaba.user.util.RandomTestUtils.Companion.randomString
import java.time.LocalDateTime

object AuthFixture {
    fun createAccessToken(
        value: String = randomString(),
        expiresAt: LocalDateTime = LocalDateTime.now(),
    ): AuthToken = AuthToken(
        type = AuthTokenType.ACCESS,
        value = value,
        expiresAt = expiresAt,
    )

    fun createRefreshToken(
        value: String = randomString(),
        expiresAt: LocalDateTime = LocalDateTime.now(),
    ): AuthToken = AuthToken(
        type = AuthTokenType.REFRESH,
        value = value,
        expiresAt = expiresAt,
    )
}
