package com.damaba.user.infrastructure.auth

import com.damaba.user.domain.auth.AuthToken
import com.damaba.user.domain.auth.AuthTokenService
import com.damaba.user.domain.auth.RefreshToken
import com.damaba.user.domain.auth.exception.InvalidAuthTokenException
import com.damaba.user.domain.user.User
import com.damaba.user.property.AuthProperties
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SignatureException
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

    override fun parseUserId(authToken: String): Long =
        getClaimsFromToken(authToken).subject.toLong()

    override fun validate(authToken: String) {
        if (authToken.isBlank()) {
            throw InvalidAuthTokenException("The token is empty")
        }
        getJwsFromToken(authToken)
    }

    private fun getClaimsFromToken(token: String): Claims =
        getJwsFromToken(token).body

    private fun getJwsFromToken(token: String): Jws<Claims> {
        try {
            return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
        } catch (ex: UnsupportedJwtException) {
            throw InvalidAuthTokenException("The claimsJws argument does not represent an Claims JWS", ex)
        } catch (ex: MalformedJwtException) {
            throw InvalidAuthTokenException("The claimsJws string is not a valid JWS", ex)
        } catch (ex: SignatureException) {
            throw InvalidAuthTokenException("The claimsJws JWS signature validation fails", ex)
        } catch (ex: ExpiredJwtException) {
            throw InvalidAuthTokenException("The Claims has an expiration time before the time this method is invoked.", ex)
        } catch (ex: IllegalArgumentException) {
            throw InvalidAuthTokenException("The claimsJws string is null or empty or only whitespace", ex)
        }
    }

    override fun createAccessToken(user: User): AuthToken =
        createToken(user, authProperties.accessTokenDurationMillis)

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
     * @param tokenDurationMillis 토큰 만료 기한(ms)
     * @return 생성된 token 정보(토큰 값, 만료 시각)
     */
    private fun createToken(user: User, tokenDurationMillis: Long): AuthToken {
        val now = Date()
        val expiresAt = Date(now.time + tokenDurationMillis)
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
