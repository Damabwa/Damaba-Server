package com.damaba.damaba.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

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
@EntityScan(
    basePackages = [
        "com.damaba.user.infra",
    ],
)
@EnableJpaRepositories(
    basePackages = [
        "com.damaba.user.infra",
    ],
)
@EnableJpaAuditing
class DamabaApiApplication

fun main(args: Array<String>) {
    runApplication<DamabaApiApplication>(*args)
}
