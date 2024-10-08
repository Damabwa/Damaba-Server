package com.damaba.damaba

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication(
    scanBasePackages = [
        "com.damaba.common_logging",
        "com.damaba.common_exception",
        "com.damaba.damaba",
        "com.damaba.user",
    ],
)
@EntityScan(basePackages = [ "com.damaba.user" ])
@EnableJpaRepositories(basePackages = [ "com.damaba.user" ])
@EnableJpaAuditing
class DamabaApiApplication

fun main(args: Array<String>) {
    runApplication<DamabaApiApplication>(*args)
}
