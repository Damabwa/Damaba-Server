package com.damaba.damaba.infrastructure.auth

import com.damaba.damaba.domain.auth.RefreshToken

interface RefreshTokenRepository {
    fun create(refreshToken: RefreshToken, ttlMillis: Long)

    fun findByUserId(userId: Long): RefreshToken?
}
