package com.damaba.user.application.port.outbound.auth

import com.damaba.user.domain.auth.RefreshToken

interface FindRefreshTokenPort {
    fun findByUserId(userId: Long): RefreshToken?
}
