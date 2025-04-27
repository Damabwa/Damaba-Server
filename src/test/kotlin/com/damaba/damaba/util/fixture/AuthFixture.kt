package com.damaba.damaba.util.fixture

import com.damaba.damaba.domain.auth.AuthToken
import com.damaba.damaba.domain.auth.AuthTokenType
import com.damaba.damaba.util.RandomTestUtils.Companion.randomString
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
