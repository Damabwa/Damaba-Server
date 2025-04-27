package com.damaba.damaba.application.port.outbound.auth

import com.damaba.damaba.domain.auth.RefreshToken

interface FindRefreshTokenPort {
    fun findByUserId(userId: Long): RefreshToken?
}
