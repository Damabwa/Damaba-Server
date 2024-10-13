package com.damaba.user.controller.auth

import com.damaba.user.application.auth.OAuthLoginUseCase
import com.damaba.user.config.ControllerTestConfig
import com.damaba.user.controller.auth.dto.OAuthLoginRequest
import com.damaba.user.domain.user.constant.LoginType
import com.damaba.user.util.RandomTestUtils.Companion.randomString
import com.damaba.user.util.TestFixture.createAuthToken
import com.damaba.user.util.TestFixture.createUser
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@Import(ControllerTestConfig::class)
@WebMvcTest(AuthController::class)
class AuthControllerTest @Autowired constructor(
    private val mvc: MockMvc,
    private val mapper: ObjectMapper,
    private val oAuthLoginUseCase: OAuthLoginUseCase,
) {
    @TestConfiguration
    class TestBeanSetUp {
        @Bean
        fun oAuthLoginUseCase() = mockk<OAuthLoginUseCase>()
    }

    @Test
    fun `(기존 회원) 카카오에서 발급받은 access token이 주어지고, 주어진 token으로 로그인한다`() {
        // given
        val kakaoAccessToken = randomString()
        val requestBody = OAuthLoginRequest(loginType = LoginType.KAKAO, authKey = kakaoAccessToken)
        val expectedResult = OAuthLoginUseCase.Result(
            isNewUser = false,
            user = createUser(),
            accessToken = createAuthToken(),
            refreshToken = createAuthToken(),
        )
        every {
            oAuthLoginUseCase.invoke(OAuthLoginUseCase.Command(requestBody.loginType, requestBody.authKey))
        } returns expectedResult

        // when & then
        mvc.perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(requestBody)),
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$.isRegistrationCompleted").value(expectedResult.user.isRegistrationCompleted))
            .andExpect(jsonPath("$.user.id").value(expectedResult.user.id))
            .andExpect(jsonPath("$.accessToken.value").value(expectedResult.accessToken.value))
            .andExpect(jsonPath("$.refreshToken.value").value(expectedResult.refreshToken.value))
        verify { oAuthLoginUseCase.invoke(OAuthLoginUseCase.Command(requestBody.loginType, requestBody.authKey)) }
    }

    @Test
    fun `(신규 회원) 카카오에서 발급받은 access token이 주어지고, 주어진 token으로 로그인한다`() {
        // given
        val kakaoAccessToken = randomString()
        val requestBody = OAuthLoginRequest(loginType = LoginType.KAKAO, authKey = kakaoAccessToken)
        val expectedResult = OAuthLoginUseCase.Result(
            isNewUser = true,
            user = createUser(),
            accessToken = createAuthToken(),
            refreshToken = createAuthToken(),
        )
        every {
            oAuthLoginUseCase.invoke(OAuthLoginUseCase.Command(requestBody.loginType, requestBody.authKey))
        } returns expectedResult

        // when & then
        mvc.perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(requestBody)),
        ).andExpect(status().isCreated)
            .andExpect(jsonPath("$.isRegistrationCompleted").value(expectedResult.user.isRegistrationCompleted))
            .andExpect(jsonPath("$.user.id").value(expectedResult.user.id))
            .andExpect(jsonPath("$.accessToken.value").value(expectedResult.accessToken.value))
            .andExpect(jsonPath("$.refreshToken.value").value(expectedResult.refreshToken.value))
        verify { oAuthLoginUseCase.invoke(OAuthLoginUseCase.Command(requestBody.loginType, requestBody.authKey)) }
    }
}
