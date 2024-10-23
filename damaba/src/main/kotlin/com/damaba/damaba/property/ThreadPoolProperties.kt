package com.damaba.damaba.property

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "thread-pool")
data class ThreadPoolProperties(
    val corePoolSize: Int,
    val maxPoolSize: Int,
    val queueCapacity: Int,
)
