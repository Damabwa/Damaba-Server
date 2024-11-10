package com.damaba.file.property

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "damaba")
data class DamabaProperties(
    val webUrl: String,
    val serverUrl: String,
    val fileServerUrl: String,
)
