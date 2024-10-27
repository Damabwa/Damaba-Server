package com.damaba.user.property

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "damaba.auth")
data class AuthProperties(
    val jwtSecret: String,
    val accessTokenDurationMillis: Long,
    val refreshTokenDurationMillis: Long,
)
