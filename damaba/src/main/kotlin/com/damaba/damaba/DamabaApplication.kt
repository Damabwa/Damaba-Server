package com.damaba.damaba

import com.damaba.user.property.AwsProperties
import com.damaba.user.property.DamabaProperties
import com.damaba.user.property.ThreadPoolProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.PropertySource
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication(
    scanBasePackages = [
        "com.damaba.common_logging",
        "com.damaba.common_exception",
        "com.damaba.damaba",
        "com.damaba.user",
    ],
)
@PropertySource("classpath:env.properties")
@EnableConfigurationProperties(DamabaProperties::class, ThreadPoolProperties::class, AwsProperties::class)
@EntityScan(basePackages = ["com.damaba.user"])
@EnableJpaRepositories(basePackages = ["com.damaba.user"])
@EnableFeignClients(basePackages = ["com.damaba.user"])
class DamabaApplication

fun main(args: Array<String>) {
    runApplication<DamabaApplication>(*args)
}
