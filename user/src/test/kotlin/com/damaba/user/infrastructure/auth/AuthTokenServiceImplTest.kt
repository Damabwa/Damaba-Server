package com.damaba.user.infrastructure.auth

import com.damaba.user.domain.auth.AuthTokenService
import com.damaba.user.domain.auth.RefreshToken
import com.damaba.user.domain.user.User
import com.damaba.user.domain.user.constant.LoginType
import com.damaba.user.property.AuthProperties
import com.damaba.user.util.RandomTestUtils.Companion.randomLong
import com.damaba.user.util.RandomTestUtils.Companion.randomString
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.api.BeforeEach
import java.time.LocalDateTime
import kotlin.test.Test

class AuthTokenServiceImplTest {
    companion object {
        private const val HOUR_MILLIS: Long = 60 * 60 * 1000
        private const val DAY_MILLIS: Long = 24 * HOUR_MILLIS
        private const val MONTH_MILLIS: Long = 30 * DAY_MILLIS
    }

    private val authProperties: AuthProperties = mockk()
    private val refreshTokenRepository: RefreshTokenRedisRepository = mockk()
    private val sut: AuthTokenService = AuthTokenServiceImpl(authProperties, refreshTokenRepository)

    @BeforeEach
    fun setup() {
        every { authProperties.jwtSecret } returns "jwtSecretForOnlyTestEnvironment12345678901234567890"
        every { authProperties.accessTokenDurationMillis } returns HOUR_MILLIS
        every { authProperties.refreshTokenDurationMillis } returns MONTH_MILLIS
        (sut as AuthTokenServiceImpl).init()
    }

    @Test
    fun `init should throw IllegalArgumentException if jwtSecret is blank`() {
        // given
        every { authProperties.jwtSecret } returns ""

        // when
        val ex = catchThrowable { (sut as AuthTokenServiceImpl).init() }

        // then
        assertThat(ex).isInstanceOf(IllegalArgumentException::class.java)
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
        every { refreshTokenRepository.save(refreshToken = any(RefreshToken::class), ttlMillis = any(Long::class)) } just Runs

        // when
        val result = sut.createRefreshToken(user)

        // then
        verify { refreshTokenRepository.save(refreshToken = any(RefreshToken::class), ttlMillis = any(Long::class)) }
        assertThat(result.value).isNotBlank()
        assertThat(result.expiresAt).isAfter(LocalDateTime.now())
    }

    private fun createUser(
        id: Long = randomLong(),
        oAuthLoginUid: String = randomString(),
        loginType: LoginType = LoginType.KAKAO,
    ): User = User(
        id = id,
        oAuthLoginUid = oAuthLoginUid,
        loginType = loginType,
    )
}
