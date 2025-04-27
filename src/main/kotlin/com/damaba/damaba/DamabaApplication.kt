package com.damaba.damaba

import com.damaba.damaba.property.AuthProperties
import com.damaba.damaba.property.AwsProperties
import com.damaba.damaba.property.DamabaProperties
import com.damaba.damaba.property.ThreadPoolProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.PropertySource

@SpringBootApplication(scanBasePackages = ["com.damaba.damaba"])
@PropertySource("classpath:env.properties")
@EnableConfigurationProperties(
    DamabaProperties::class,
    AuthProperties::class,
    ThreadPoolProperties::class,
    AwsProperties::class,
)
@EntityScan(basePackages = ["com.damaba.damaba"])
@EnableFeignClients(basePackages = ["com.damaba.damaba"])
class DamabaApplication

fun main(args: Array<String>) {
    runApplication<DamabaApplication>(*args)
}
