package com.damaba.user

import com.damaba.user.property.AuthProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@SpringBootApplication(scanBasePackages = ["com.damaba"])
@EnableFeignClients
@EnableConfigurationProperties(AuthProperties::class)
class DamabaUserTestApplication
