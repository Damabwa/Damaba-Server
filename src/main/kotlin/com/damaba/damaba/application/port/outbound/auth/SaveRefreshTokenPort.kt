package com.damaba.damaba.application.port.outbound.auth

import com.damaba.damaba.domain.auth.RefreshToken

interface SaveRefreshTokenPort {
    fun save(refreshToken: RefreshToken, ttlMillis: Long)
}
