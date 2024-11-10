package com.damaba.file

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.test.context.ActiveProfiles

// @EnableConfigurationProperties(
//    DamabaProperties::class,
//    AuthProperties::class,
// )
@ActiveProfiles("test")
@SpringBootApplication(scanBasePackages = ["com.damaba"])
class DamabaFileTestApplication
