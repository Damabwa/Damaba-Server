package com.damaba.damaba.application.port.outbound.auth

import com.damaba.damaba.domain.auth.RefreshToken

interface CreateRefreshTokenPort {
    fun create(refreshToken: RefreshToken, ttlMillis: Long)
}
