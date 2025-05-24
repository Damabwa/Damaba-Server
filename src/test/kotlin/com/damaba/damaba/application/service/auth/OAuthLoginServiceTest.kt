package com.damaba.damaba.application.service.auth

import com.damaba.damaba.application.port.inbound.auth.OAuthLoginUseCase
import com.damaba.damaba.domain.auth.RefreshToken
import com.damaba.damaba.domain.user.User
import com.damaba.damaba.domain.user.constant.LoginType
import com.damaba.damaba.infrastructure.auth.AuthTokenManager
import com.damaba.damaba.infrastructure.auth.OAuthLoginProvider
import com.damaba.damaba.infrastructure.auth.RefreshTokenRepository
import com.damaba.damaba.infrastructure.user.UserRepository
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
    private val authTokenManager: AuthTokenManager = mockk()
    private val oAuthLoginProvider: OAuthLoginProvider = mockk()
    private val refreshTokenRepo: RefreshTokenRepository = mockk()
    private val userRepo: UserRepository = mockk()
    private val authProperties: AuthProperties = mockk()
    private val sut: OAuthLoginService = OAuthLoginService(
        authTokenManager,
        oAuthLoginProvider,
        refreshTokenRepo,
        userRepo,
        authProperties,
    )

    @BeforeEach
    fun setup() {
        every { authProperties.refreshTokenDurationMillis } returns REFRESH_TOKEN_DURATION_MILLIS
    }

    private fun confirmVerifiedEveryMocks() {
        confirmVerified(
            authTokenManager,
            oAuthLoginProvider,
            refreshTokenRepo,
            userRepo,
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
            oAuthLoginProvider.getOAuthLoginUid(loginType, kakaoAccessToken)
        } returns kakaoUserId
        every {
            userRepo.findByOAuthLoginUid(kakaoUserId)
        } returns null
        every {
            userRepo.existsNickname(any(String::class))
        } returns true andThen false
        every {
            userRepo.create(any(User::class))
        } returns newUser
        every {
            authTokenManager.createAccessToken(newUser)
        } returns accessToken
        every {
            authTokenManager.createRefreshToken(newUser)
        } returns refreshToken
        every {
            refreshTokenRepo.create(RefreshToken(newUser.id, refreshToken.value), REFRESH_TOKEN_DURATION_MILLIS)
        } just Runs

        // when
        val result = sut.oAuthLogin(OAuthLoginUseCase.Command(loginType = loginType, authKey = kakaoAccessToken))

        // then
        verifyOrder {
            oAuthLoginProvider.getOAuthLoginUid(loginType, kakaoAccessToken)
            userRepo.findByOAuthLoginUid(kakaoUserId)
            userRepo.existsNickname(any(String::class))
            userRepo.existsNickname(any(String::class))
            userRepo.create(any(User::class))
            authTokenManager.createAccessToken(newUser)
            authTokenManager.createRefreshToken(newUser)
            refreshTokenRepo.create(RefreshToken(newUser.id, refreshToken.value), REFRESH_TOKEN_DURATION_MILLIS)
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

        every { oAuthLoginProvider.getOAuthLoginUid(loginType, kakaoAccessToken) } returns kakaoUserId
        every { userRepo.findByOAuthLoginUid(kakaoUserId) } returns user
        every { authTokenManager.createAccessToken(user) } returns accessToken
        every { authTokenManager.createRefreshToken(user) } returns refreshToken
        every {
            refreshTokenRepo.create(RefreshToken(user.id, refreshToken.value), REFRESH_TOKEN_DURATION_MILLIS)
        } just Runs

        // when
        val result = sut.oAuthLogin(OAuthLoginUseCase.Command(loginType = loginType, authKey = kakaoAccessToken))

        // then
        verifyOrder {
            oAuthLoginProvider.getOAuthLoginUid(loginType, kakaoAccessToken)
            userRepo.findByOAuthLoginUid(kakaoUserId)
            authTokenManager.createAccessToken(user)
            authTokenManager.createRefreshToken(user)
            refreshTokenRepo.create(RefreshToken(user.id, refreshToken.value), REFRESH_TOKEN_DURATION_MILLIS)
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
