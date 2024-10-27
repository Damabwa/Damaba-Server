package com.damaba.user.application.port.outbound.auth

import com.damaba.user.domain.auth.RefreshToken

interface SaveRefreshTokenPort {
    fun save(refreshToken: RefreshToken, ttlMillis: Long)
}
