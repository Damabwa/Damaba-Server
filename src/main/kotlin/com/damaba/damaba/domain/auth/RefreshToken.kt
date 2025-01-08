package com.damaba.damaba.domain.auth

// RTR(Refresh token rotation) 전략 사용
data class RefreshToken(
    val userId: Long,
    val token: String,
)
