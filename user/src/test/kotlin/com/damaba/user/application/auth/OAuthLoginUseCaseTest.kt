package com.damaba.user.application.auth

import com.damaba.common_exception.ValidationException
import com.damaba.user.domain.auth.AuthTokenService
import com.damaba.user.domain.auth.OAuthService
import com.damaba.user.domain.user.UserService
import com.damaba.user.domain.user.constant.LoginType
import com.damaba.user.util.RandomTestUtils.Companion.randomString
import com.damaba.user.util.TestFixture.createAuthToken
import com.damaba.user.util.TestFixture.createUser
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifyOrder
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import kotlin.test.Test

class OAuthLoginUseCaseTest {
    private val oAuthService = mockk<OAuthService>()
    private val authTokenService = mockk<AuthTokenService>()
    private val userService = mockk<UserService>()
    private val sut = OAuthLoginUseCase(oAuthService, authTokenService, userService)

    @Test
    fun `(신규 유저) Kakao에서 발행한 access token이 주어지고, 주어진 token으로 유저 정보 조회 및 회원가입을 진행한다`() {
        // given
        val loginType = LoginType.KAKAO
        val kakaoAccessToken = "kakao-access-token"
        val kakaoUserId = randomString()

        val newUser = createUser()
        val accessToken = createAuthToken()
        val refreshToken = createAuthToken()

        every { oAuthService.getOAuthLoginUid(loginType, kakaoAccessToken) } returns kakaoUserId
        every { userService.findUserByOAuthLoginUid(kakaoUserId) } returns null
        every { userService.createNewUser(kakaoUserId, loginType) } returns newUser
        every { authTokenService.createAccessToken(newUser) } returns accessToken
        every { authTokenService.createRefreshToken(newUser) } returns refreshToken

        // when
        val result = sut.invoke(OAuthLoginUseCase.Command(loginType = loginType, authKey = kakaoAccessToken))

        // then
        verifyOrder {
            oAuthService.getOAuthLoginUid(loginType, kakaoAccessToken)
            userService.findUserByOAuthLoginUid(kakaoUserId)
            userService.createNewUser(kakaoUserId, loginType)
            authTokenService.createAccessToken(newUser)
            authTokenService.createRefreshToken(newUser)
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
        val accessToken = createAuthToken()
        val refreshToken = createAuthToken()

        every { oAuthService.getOAuthLoginUid(loginType, kakaoAccessToken) } returns kakaoUserId
        every { userService.findUserByOAuthLoginUid(kakaoUserId) } returns user
        every { authTokenService.createAccessToken(user) } returns accessToken
        every { authTokenService.createRefreshToken(user) } returns refreshToken

        // when
        val result = sut.invoke(OAuthLoginUseCase.Command(loginType = loginType, authKey = kakaoAccessToken))

        // then
        verifyOrder {
            oAuthService.getOAuthLoginUid(loginType, kakaoAccessToken)
            userService.findUserByOAuthLoginUid(kakaoUserId)
            authTokenService.createAccessToken(user)
            authTokenService.createRefreshToken(user)
        }
        confirmVerifiedEveryMocks()
        assertThat(result.user).isEqualTo(user)
        assertThat(result.accessToken).isEqualTo(accessToken)
        assertThat(result.refreshToken).isEqualTo(refreshToken)
    }

    @Test
    fun `auth key가 공백으로 주어지고, OAuth 로그인을 진행하면, validation 예외가 발생한다`() {
        // given
        val authKey = ""

        // when
        val ex = catchThrowable {
            sut.invoke(OAuthLoginUseCase.Command(loginType = LoginType.KAKAO, authKey = authKey))
        }

        // then
        confirmVerifiedEveryMocks()
        assertThat(ex).isInstanceOf(ValidationException::class.java)
    }

    private fun confirmVerifiedEveryMocks() {
        confirmVerified(oAuthService, authTokenService, userService)
    }
}
