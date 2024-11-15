package com.damaba.user.util

import com.damaba.user.domain.auth.AuthToken
import com.damaba.user.util.RandomTestUtils.Companion.randomString
import java.time.LocalDateTime

object AuthFixture {
    fun createAuthToken(
        value: String = randomString(),
        expiresAt: LocalDateTime = LocalDateTime.now(),
    ): AuthToken = AuthToken(
        value = value,
        expiresAt = expiresAt,
    )
}
