package com.damaba.damaba.domain.auth

import com.damaba.damaba.domain.auth.constant.AuthTokenType
import java.time.LocalDateTime

data class AuthToken(
    val type: AuthTokenType,
    val value: String,
    val expiresAt: LocalDateTime,
)
