package com.damaba.damaba.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(
    scanBasePackages = [
        "com.damaba.common_logging",
        "com.damaba.common_exception",
        "com.damaba.damaba.api",
        "com.damaba.user.api",
        "com.damaba.user.domain",
        "com.damaba.user.infra",
    ],
)
class DamabaApiApplication

fun main(args: Array<String>) {
    runApplication<DamabaApiApplication>(*args)
}
