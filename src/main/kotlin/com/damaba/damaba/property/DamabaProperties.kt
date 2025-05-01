package com.damaba.damaba.property

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "damaba")
data class DamabaProperties(
    val corsAllowedOrigins: String,
    val serverUrl: String,
    val fileServerUrl: String,
)
