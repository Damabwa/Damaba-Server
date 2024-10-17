package com.damaba.user.property

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "damaba")
data class DamabaProperties(
    val fileServerUrl: String,
    val auth: AuthProperties,
) {
    data class AuthProperties(
        val jwtSecret: String,
        val accessTokenDurationMillis: Long,
        val refreshTokenDurationMillis: Long,
    )
}
