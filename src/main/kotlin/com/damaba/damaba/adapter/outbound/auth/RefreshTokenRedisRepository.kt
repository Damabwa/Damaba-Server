package com.damaba.damaba.adapter.outbound.auth

import com.damaba.damaba.application.port.outbound.auth.FindRefreshTokenPort
import com.damaba.damaba.application.port.outbound.auth.SaveRefreshTokenPort
import com.damaba.damaba.domain.auth.RefreshToken
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import java.util.concurrent.TimeUnit

@Repository
class RefreshTokenRedisRepository(
    private val redisTemplate: RedisTemplate<String, String>,
) : FindRefreshTokenPort,
    SaveRefreshTokenPort {
    override fun findByUserId(userId: Long): RefreshToken? {
        val tokenValue = redisTemplate.opsForValue().get(KEY_PREFIX + userId)
        return if (tokenValue != null) RefreshToken(userId, tokenValue) else null
    }

    override fun save(refreshToken: RefreshToken, ttlMillis: Long) {
        redisTemplate.opsForValue()
            .set(KEY_PREFIX + refreshToken.userId, refreshToken.token, ttlMillis, TimeUnit.MILLISECONDS)
    }

    companion object {
        private const val KEY_PREFIX = "refresh_token:"
    }
}
