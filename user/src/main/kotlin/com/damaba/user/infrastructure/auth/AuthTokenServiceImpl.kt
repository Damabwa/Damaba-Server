package com.damaba.user.infrastructure.auth

import com.damaba.user.domain.auth.AuthToken
import com.damaba.user.domain.auth.AuthTokenService
import com.damaba.user.domain.auth.RefreshToken
import com.damaba.user.domain.user.User
import com.damaba.user.property.AuthProperties
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service
import java.security.Key
import java.time.ZoneId
import java.util.Date

@Service
class AuthTokenServiceImpl(
    private val authProperties: AuthProperties,
    private val refreshTokenRepository: RefreshTokenRedisRepository,
) : AuthTokenService {
    companion object {
        private const val USER_ROLE_CLAIM_KEY = "role"
    }

    private lateinit var secretKey: Key

    @PostConstruct
    fun init() {
        val secret = authProperties.jwtSecret
        if (secret.isBlank()) {
            throw IllegalArgumentException("JWT token 생성 및 검증에 필요한 secret key(salt)가 입력되지 않았습니다.")
        }
        secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret))
    }

    /**
     * Access token 생성 및 반환한다.
     *
     * @param user
     * @return 생성된 access token 정보(토큰 값, 만료 시각)
     */
    override fun createAccessToken(user: User): AuthToken =
        createToken(user, authProperties.accessTokenDurationMillis)

    /**
     * Refresh token을 생성 및 반환한다.
     * 생성된 refresh token은 redis에 저장된다.
     *
     * @param user
     * @return 생성된 refresh token 정보(토큰 값, 만료 시각)
     */
    override fun createRefreshToken(user: User): AuthToken {
        val token = createToken(user, authProperties.refreshTokenDurationMillis)
        refreshTokenRepository.save(
            refreshToken = RefreshToken(user.id, token.value),
            ttlMillis = authProperties.refreshTokenDurationMillis,
        )
        return token
    }

    /**
     * JWT 생성
     *
     * @param user 토큰에 담을 유저 정보
     * @param tokenDuration 토큰 만료 기한
     * @return 생성된 token 정보(토큰 값, 만료 시각)
     */
    private fun createToken(user: User, tokenDuration: Long): AuthToken {
        val now = Date()
        val expiresAt = Date(now.time + tokenDuration)
        val token = Jwts.builder()
            .setHeaderParam("typ", "JWT")
            .setSubject(user.id.toString())
            .claim(USER_ROLE_CLAIM_KEY, user.roles)
            .setIssuedAt(now)
            .setExpiration(expiresAt)
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact()
        return AuthToken(
            value = token,
            expiresAt = expiresAt.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
        )
    }
}
