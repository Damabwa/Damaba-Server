package com.damaba.user.domain.auth

import java.time.LocalDateTime

data class AuthToken(
    val type: AuthTokenType,
    val value: String,
    val expiresAt: LocalDateTime,
)
