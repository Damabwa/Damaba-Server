package com.damaba.damaba.adapter.outbound.auth

import com.damaba.damaba.application.port.outbound.auth.CreateAuthTokenPort
import com.damaba.damaba.application.port.outbound.auth.ParseUserIdFromAuthTokenPort
import com.damaba.damaba.application.port.outbound.auth.ValidateAuthTokenPort
import com.damaba.damaba.domain.auth.AuthToken
import com.damaba.damaba.domain.auth.constant.AuthTokenType
import com.damaba.damaba.domain.auth.exception.InvalidAuthTokenException
import com.damaba.damaba.domain.user.User
import com.damaba.damaba.property.AuthProperties
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component
import java.security.Key
import java.time.ZoneId
import java.util.Date

@Component
class AuthTokenAdapter(private val authProperties: AuthProperties) :
    CreateAuthTokenPort,
    ParseUserIdFromAuthTokenPort,
    ValidateAuthTokenPort {

    private lateinit var secretKey: Key

    @PostConstruct
    fun init() {
        val secret = authProperties.jwtSecret
        if (secret.isBlank()) {
            throw IllegalArgumentException("JWT token 생성 및 검증에 필요한 secret key(salt)가 입력되지 않았습니다.")
        }
        secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret))
    }

    override fun parseUserId(authToken: String): Long = getClaimsFromToken(authToken).subject.toLong()

    override fun validateAccessToken(authToken: String) {
        if (authToken.isBlank()) {
            throw InvalidAuthTokenException("The token is empty")
        }

        val tokenType = getClaimsFromToken(authToken)[TOKEN_TYPE_CLAIM_KEY] as? String
            ?: throw InvalidAuthTokenException("The token does not contain a type claim")

        if (tokenType != AuthTokenType.ACCESS.name) {
            throw InvalidAuthTokenException("The token is not access token")
        }
    }

    private fun getClaimsFromToken(token: String): Claims {
        try {
            return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .body
        } catch (ex: Exception) {
            throw InvalidAuthTokenException(cause = ex)
        }
    }

    override fun createAccessToken(user: User): AuthToken = createToken(AuthTokenType.ACCESS, user, authProperties.accessTokenDurationMillis)

    override fun createRefreshToken(user: User): AuthToken = createToken(AuthTokenType.REFRESH, user, authProperties.refreshTokenDurationMillis)

    /**
     * JWT 생성
     *
     * @param tokenType 생성할 토큰 유형
     * @param user 토큰에 담을 유저 정보
     * @param tokenDurationMillis 토큰 만료 기한(ms)
     * @return 생성된 token 정보(토큰 값, 만료 시각)
     */
    private fun createToken(tokenType: AuthTokenType, user: User, tokenDurationMillis: Long): AuthToken {
        val now = Date()
        val expiresAt = Date(now.time + tokenDurationMillis)
        val token = Jwts.builder()
            .setHeaderParam("typ", "JWT")
            .setSubject(user.id.toString())
            .claim(TOKEN_TYPE_CLAIM_KEY, tokenType.name)
            .claim(USER_ROLE_CLAIM_KEY, user.roles)
            .setIssuedAt(now)
            .setExpiration(expiresAt)
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact()
        return AuthToken(
            value = token,
            type = tokenType,
            expiresAt = expiresAt.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
        )
    }

    companion object {
        private const val USER_ROLE_CLAIM_KEY = "role"
        private const val TOKEN_TYPE_CLAIM_KEY = "type"
    }
}
