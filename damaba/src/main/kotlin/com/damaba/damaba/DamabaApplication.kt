package com.damaba.damaba

import com.damaba.damaba.property.AwsProperties
import com.damaba.damaba.property.ThreadPoolProperties
import com.damaba.user.property.AuthProperties
import com.damaba.user.property.DamabaProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.PropertySource

@SpringBootApplication(
    scanBasePackages = [
        "com.damaba.common_logging",
        "com.damaba.common_exception",
        "com.damaba.damaba",
        "com.damaba.user",
    ],
)
@PropertySource("classpath:env.properties")
@EnableConfigurationProperties(
    DamabaProperties::class,
    AuthProperties::class,
    ThreadPoolProperties::class,
    AwsProperties::class,
)
@EntityScan(basePackages = ["com.damaba.user", "com.damaba.damaba"])
@EnableFeignClients(basePackages = ["com.damaba.user"])
class DamabaApplication

fun main(args: Array<String>) {
    runApplication<DamabaApplication>(*args)
}
