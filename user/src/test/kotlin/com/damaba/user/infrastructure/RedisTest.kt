package com.damaba.user.infrastructure

import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@ActiveProfiles("test")
@Testcontainers
@DataRedisTest
abstract class RedisTest {
    companion object {
        private const val DOCKER_REDIS_IMAGE = "redis:7.4.1-alpine"

        @Container
        val redisContainer =
            GenericContainer<Nothing>(DOCKER_REDIS_IMAGE).apply { withExposedPorts(6379) }

        @JvmStatic
        @DynamicPropertySource
        fun redisProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.data.redis.host", redisContainer::getHost)
            registry.add("spring.data.redis.port") { redisContainer.getMappedPort(6379).toString() }
        }
    }
}
