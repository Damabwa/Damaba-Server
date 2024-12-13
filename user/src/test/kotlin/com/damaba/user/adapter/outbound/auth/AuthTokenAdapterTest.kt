package com.damaba.user.adapter.outbound.auth

import com.damaba.user.domain.auth.exception.InvalidAuthTokenException
import com.damaba.user.property.AuthProperties
import com.damaba.user.util.UserFixture.createUser
import io.jsonwebtoken.Jwts
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.api.BeforeEach
import org.springframework.test.util.ReflectionTestUtils
import java.security.Key
import java.time.LocalDateTime
import java.util.Date
import kotlin.test.Test

class AuthTokenAdapterTest {
    private val authProperties: AuthProperties = mockk()
    private val sut: AuthTokenAdapter = AuthTokenAdapter(authProperties)

    @BeforeEach
    fun setup() {
        every { authProperties.jwtSecret } returns "jwtSecretForOnlyTestEnvironment12345678901234567890"
        every { authProperties.accessTokenDurationMillis } returns HOUR_MILLIS
        every { authProperties.refreshTokenDurationMillis } returns MONTH_MILLIS
        sut.init()
    }

    @Test
    fun `jwtSecret이 비어있을 때, init 메서드가 초기화 로직을 수행하면, 예외가 발생한다`() {
        // given
        every { authProperties.jwtSecret } returns ""

        // when
        val ex = catchThrowable { sut.init() }

        // then
        assertThat(ex).isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `유효한 access 토큰이 주어지고, 주어진 access 토큰의 유효성을 검증하면, 아무 일도 발생하지 않는다`() {
        // given
        val validToken = sut.createAccessToken(createUser()).value

        // when & then
        assertThatCode { sut.validateAccessToken(validToken) }.doesNotThrowAnyException()
    }

    @Test
    fun `유효한 refresh 토큰이 주어지고, 주어진 access 토큰의 유효성을 검증하면, 예외가 발생한다`() {
        // given
        val token = sut.createRefreshToken(createUser()).value

        // when
        val ex = catchThrowable { sut.validateAccessToken(token) }

        // then
        assertThat(ex).isInstanceOf(InvalidAuthTokenException::class.java)
    }

    @Test
    fun `빈 문자열로 토큰 값이 주어지고, 주어진 access 토큰의 유효성을 검증하면, 예외가 발생한다`() {
        // given
        val token = ""

        // when
        val ex = catchThrowable { sut.validateAccessToken(token) }

        // then
        assertThat(ex).isInstanceOf(InvalidAuthTokenException::class.java)
    }

    @Test
    fun `토큰 유형 claim이 없는 토큰이 주어지고, 주어진 access 토큰의 유효성을 검증하면, 예외가 발생한다`() {
        // given
        val now = Date()
        val secretKey = ReflectionTestUtils.getField(sut, "secretKey") as Key
        val token = Jwts.builder()
            .setHeaderParam("typ", "JWT")
            .setSubject(1L.toString())
            .setIssuedAt(now)
            .setExpiration(Date(now.time + HOUR_MILLIS))
            .signWith(secretKey)
            .compact()

        // when
        val ex = catchThrowable { sut.validateAccessToken(token) }

        // then
        assertThat(ex).isInstanceOf(InvalidAuthTokenException::class.java)
    }

    @Test
    fun `유효하지 않은 토큰 값이 주어지고, 주어진 토큰의 유효성을 검증하면, 예외가 발생한다`() {
        // given
        val token = "Invalid token"

        // when
        val ex = catchThrowable { sut.validateAccessToken(token) }

        // then
        assertThat(ex).isInstanceOf(InvalidAuthTokenException::class.java)
    }

    @Test
    fun `유효한 토큰이 주어지고, 토큰에서 user id를 파싱하면, 추출된 user id가 반환된다`() {
        // given
        val user = createUser()
        val token = sut.createAccessToken(user).value

        // when
        val result = sut.parseUserId(token)

        // then
        assertThat(result).isEqualTo(user.id)
    }

    @Test
    fun `잘못된 토큰이 주어지고, 토큰에서 user id를 파싱하면, 예외가 발생한다`() {
        // given
        val invalidToken = "invalidToken"

        // when
        val ex = catchThrowable { sut.parseUserId(invalidToken) }

        // then
        assertThat(ex).isInstanceOf(InvalidAuthTokenException::class.java)
    }

    @Test
    fun `유저가 주어지고, 주어진 유저 정보로 access token을 생성하면, 생성된 access token 정보가 반환된다`() {
        // given
        val user = createUser()

        // when
        val result = sut.createAccessToken(user)

        // then
        assertThat(result.value).isNotBlank()
        assertThat(result.expiresAt).isAfter(LocalDateTime.now())
    }

    @Test
    fun `유저가 주어지고, 주어진 유저 정보로 refresh token을 생성하면, 생성된 refresh token 정보가 반환된다`() {
        // given
        val user = createUser()

        // when
        val result = sut.createRefreshToken(user)

        // then
        assertThat(result.value).isNotBlank()
        assertThat(result.expiresAt).isAfter(LocalDateTime.now())
    }

    companion object {
        private const val HOUR_MILLIS: Long = 60 * 60 * 1000
        private const val DAY_MILLIS: Long = 24 * HOUR_MILLIS
        private const val MONTH_MILLIS: Long = 30 * DAY_MILLIS
    }
}
