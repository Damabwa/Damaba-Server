package com.damaba.damaba.adapter.outbound.auth

import com.damaba.damaba.config.RedisTest
import com.damaba.damaba.domain.auth.RefreshToken
import com.damaba.damaba.util.RandomTestUtils.Companion.randomLong
import com.damaba.damaba.util.RandomTestUtils.Companion.randomString
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate

class RefreshTokenRedisRepositoryTest @Autowired constructor(
    private val redisTemplate: RedisTemplate<String, String>,
) : RedisTest() {
    companion object {
        private const val REFRESH_TOKEN_TTL = 60 * 1000L
    }

    private val sut: RefreshTokenRedisRepository = RefreshTokenRedisRepository(redisTemplate)

    @BeforeEach
    fun redisInit() {
        redisTemplate.execute { con -> con.serverCommands().flushDb() }
    }

    @Test
    fun `Refresh token이 주어지고, 주어진 refresh token을 저장한다`() {
        // Given
        val refreshToken = RefreshToken(
            userId = randomLong(),
            token = randomString(),
        )

        // When
        sut.save(refreshToken, REFRESH_TOKEN_TTL)

        // Then
        val savedRefreshToken = sut.findByUserId(refreshToken.userId)
        assertThat(savedRefreshToken).isNotNull()
        assertThat(savedRefreshToken).isEqualTo(refreshToken)
    }

    @Test
    fun `저장된 refresh token의 user id가 주어지고, 주어진 id로 refresh token을 조회한다`() {
        // Given
        val userId = randomLong()
        val expectedResult = RefreshToken(userId, randomString())
        sut.save(expectedResult, REFRESH_TOKEN_TTL)

        // When
        val actualResult = sut.findByUserId(userId)

        // Then
        assertThat(actualResult).isNotNull()
        assertThat(actualResult).isEqualTo(expectedResult)
    }

    @Test
    fun `유효하지 않은 user id가 주어지고, 주어진 id로 refresh token을 조회하면, null이 반환된다`() {
        // Given
        val userId = randomLong()

        // When
        val result = sut.findByUserId(userId)

        // Then
        assertThat(result).isNull()
    }
}
