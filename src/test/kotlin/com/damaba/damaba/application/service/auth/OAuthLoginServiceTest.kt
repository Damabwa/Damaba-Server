package com.damaba.damaba.application.service.auth

import com.damaba.damaba.application.port.inbound.auth.OAuthLoginUseCase
import com.damaba.damaba.application.port.outbound.auth.CreateAuthTokenPort
import com.damaba.damaba.application.port.outbound.auth.GetOAuthLoginUidPort
import com.damaba.damaba.application.port.outbound.auth.SaveRefreshTokenPort
import com.damaba.damaba.application.port.outbound.user.CheckNicknameExistencePort
import com.damaba.damaba.application.port.outbound.user.FindUserPort
import com.damaba.damaba.application.port.outbound.user.SaveUserPort
import com.damaba.damaba.domain.auth.RefreshToken
import com.damaba.damaba.domain.user.User
import com.damaba.damaba.domain.user.constant.LoginType
import com.damaba.damaba.property.AuthProperties
import com.damaba.damaba.util.RandomTestUtils.Companion.randomString
import com.damaba.damaba.util.fixture.AuthFixture.createAccessToken
import com.damaba.damaba.util.fixture.AuthFixture.createRefreshToken
import com.damaba.damaba.util.fixture.UserFixture.createUser
import io.mockk.Runs
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verifyOrder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test

class OAuthLoginServiceTest {
    private val getOAuthLoginUidPort: GetOAuthLoginUidPort = mockk()
    private val findUserPort: FindUserPort = mockk()
    private val checkNicknameExistencePort: CheckNicknameExistencePort = mockk()
    private val saveUserPort: SaveUserPort = mockk()
    private val createAuthTokenPort: CreateAuthTokenPort = mockk()
    private val saveRefreshTokenPort: SaveRefreshTokenPort = mockk()
    private val authProperties: AuthProperties = mockk()
    private val sut: OAuthLoginService = OAuthLoginService(
        getOAuthLoginUidPort,
        findUserPort,
        checkNicknameExistencePort,
        saveUserPort,
        createAuthTokenPort,
        saveRefreshTokenPort,
        authProperties,
    )

    @BeforeEach
    fun setup() {
        every { authProperties.refreshTokenDurationMillis } returns REFRESH_TOKEN_DURATION_MILLIS
    }

    private fun confirmVerifiedEveryMocks() {
        confirmVerified(
            getOAuthLoginUidPort,
            findUserPort,
            checkNicknameExistencePort,
            saveUserPort,
            createAuthTokenPort,
            saveRefreshTokenPort,
        )
    }

    @Test
    fun `(신규 유저) Kakao에서 발행한 access token이 주어지고, 주어진 token으로 유저 정보 조회 및 회원가입을 진행한다`() {
        // given
        val loginType = LoginType.KAKAO
        val kakaoAccessToken = "kakao-access-token"
        val kakaoUserId = randomString()

        val newUser = createUser()
        val accessToken = createAccessToken()
        val refreshToken = createRefreshToken()

        every {
            getOAuthLoginUidPort.getOAuthLoginUid(loginType, kakaoAccessToken)
        } returns kakaoUserId
        every {
            findUserPort.findByOAuthLoginUid(kakaoUserId)
        } returns null
        every {
            checkNicknameExistencePort.doesNicknameExist(any(String::class))
        } returns true andThen false
        every {
            saveUserPort.save(any(User::class))
        } returns newUser
        every {
            createAuthTokenPort.createAccessToken(newUser)
        } returns accessToken
        every {
            createAuthTokenPort.createRefreshToken(newUser)
        } returns refreshToken
        every {
            saveRefreshTokenPort.save(RefreshToken(newUser.id, refreshToken.value), REFRESH_TOKEN_DURATION_MILLIS)
        } just Runs

        // when
        val result = sut.oAuthLogin(OAuthLoginUseCase.Command(loginType = loginType, authKey = kakaoAccessToken))

        // then
        verifyOrder {
            getOAuthLoginUidPort.getOAuthLoginUid(loginType, kakaoAccessToken)
            findUserPort.findByOAuthLoginUid(kakaoUserId)
            checkNicknameExistencePort.doesNicknameExist(any(String::class))
            checkNicknameExistencePort.doesNicknameExist(any(String::class))
            saveUserPort.save(any(User::class))
            createAuthTokenPort.createAccessToken(newUser)
            createAuthTokenPort.createRefreshToken(newUser)
            saveRefreshTokenPort.save(RefreshToken(newUser.id, refreshToken.value), REFRESH_TOKEN_DURATION_MILLIS)
        }
        confirmVerifiedEveryMocks()
        assertThat(result.user).isEqualTo(newUser)
        assertThat(result.accessToken).isEqualTo(accessToken)
        assertThat(result.refreshToken).isEqualTo(refreshToken)
    }

    @Test
    fun `(기존 유저) Kakao에서 발행한 access token이 주어지고, 주어진 token으로 유저 정보 조회 및 로그인을 진행한다`() {
        // given
        val loginType = LoginType.KAKAO
        val kakaoAccessToken = "kakao-access-token"
        val kakaoUserId = randomString()

        val user = createUser()
        val accessToken = createAccessToken()
        val refreshToken = createRefreshToken()

        every { getOAuthLoginUidPort.getOAuthLoginUid(loginType, kakaoAccessToken) } returns kakaoUserId
        every { findUserPort.findByOAuthLoginUid(kakaoUserId) } returns user
        every { createAuthTokenPort.createAccessToken(user) } returns accessToken
        every { createAuthTokenPort.createRefreshToken(user) } returns refreshToken
        every {
            saveRefreshTokenPort.save(RefreshToken(user.id, refreshToken.value), REFRESH_TOKEN_DURATION_MILLIS)
        } just Runs

        // when
        val result = sut.oAuthLogin(OAuthLoginUseCase.Command(loginType = loginType, authKey = kakaoAccessToken))

        // then
        verifyOrder {
            getOAuthLoginUidPort.getOAuthLoginUid(loginType, kakaoAccessToken)
            findUserPort.findByOAuthLoginUid(kakaoUserId)
            createAuthTokenPort.createAccessToken(user)
            createAuthTokenPort.createRefreshToken(user)
            saveRefreshTokenPort.save(RefreshToken(user.id, refreshToken.value), REFRESH_TOKEN_DURATION_MILLIS)
        }
        confirmVerifiedEveryMocks()
        assertThat(result.user).isEqualTo(user)
        assertThat(result.accessToken).isEqualTo(accessToken)
        assertThat(result.refreshToken).isEqualTo(refreshToken)
    }

    companion object {
        private const val REFRESH_TOKEN_DURATION_MILLIS = 1000L * 60 * 60 * 24 * 30
    }
}
