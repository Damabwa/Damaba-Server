package com.damaba.user.controller.user

import com.damaba.user.application.user.CheckNicknameAvailabilityUseCase
import com.damaba.user.application.user.GetMyInfoUseCase
import com.damaba.user.application.user.UpdateMyInfoUseCase
import com.damaba.user.config.ControllerTestConfig
import com.damaba.user.controller.user.dto.UpdateMyInfoRequest
import com.damaba.user.domain.user.constant.Gender
import com.damaba.user.util.RandomTestUtils.Companion.randomBoolean
import com.damaba.user.util.RandomTestUtils.Companion.randomInt
import com.damaba.user.util.RandomTestUtils.Companion.randomLong
import com.damaba.user.util.RandomTestUtils.Companion.randomString
import com.damaba.user.util.TestAuthUtils.createAuthenticationToken
import com.damaba.user.util.TestFixture.createUser
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import kotlin.test.Test

@Import(ControllerTestConfig::class)
@WebMvcTest(UserController::class)
class UserControllerTest @Autowired constructor(
    private val mvc: MockMvc,
    private val mapper: ObjectMapper,
    private val getMyInfoUseCase: GetMyInfoUseCase,
    private val checkNicknameAvailabilityUseCase: CheckNicknameAvailabilityUseCase,
    private val updateMyInfoUseCase: UpdateMyInfoUseCase,
) {
    @TestConfiguration
    class TestBeanSetUp {
        @Bean
        fun getMyInfoUseCase(): GetMyInfoUseCase = mockk()

        @Bean
        fun checkNicknameAvailabilityUseCase(): CheckNicknameAvailabilityUseCase = mockk()

        @Bean
        fun updateMyInfoUseCase(): UpdateMyInfoUseCase = mockk()
    }

    @Test
    fun `내 정보를 조회하면, 내 정보가 응답된다`() {
        // given
        val userId = randomLong()
        val me = createUser(id = userId)
        every { getMyInfoUseCase.invoke(userId) } returns me

        // when & then
        mvc.perform(
            get("/api/v1/users/me")
                .with(authentication(createAuthenticationToken(me))),
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(me.id))
            .andExpect(jsonPath("$.nickname").value(me.nickname))
    }

    @Test
    fun `수정할 유저 정보가 주어지고, 유저 정보를 수정하면, 수정된 유저 정보가 응답된다`() {
        // given
        val requestUser = createUser()
        val request = UpdateMyInfoRequest(
            nickname = randomString(),
            gender = Gender.FEMALE,
            age = randomInt(),
            instagramId = randomString(),
        )
        val expectedResult = createUser(
            id = requestUser.id,
            nickname = request.nickname!!,
            gender = request.gender!!,
            age = request.age!!,
            instagramId = request.instagramId!!,
        )
        every { updateMyInfoUseCase.invoke(request.toCommand(requestUser.id)) } returns expectedResult

        // when & then
        mvc.perform(
            patch("/api/v1/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
                .with(authentication(createAuthenticationToken(requestUser))),
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(requestUser.id))
            .andExpect(jsonPath("$.nickname").value(expectedResult.nickname))
            .andExpect(jsonPath("$.gender").value(expectedResult.gender.toString()))
            .andExpect(jsonPath("$.age").value(expectedResult.age))
            .andExpect(jsonPath("$.instagramId").value(expectedResult.instagramId))
        verify { updateMyInfoUseCase.invoke(request.toCommand(requestUser.id)) }
    }

    @Test
    fun `닉네임이 주어지고, 주어진 닉네임의 이용가능성을 확인한다`() {
        // given
        val nickname = randomString()
        val expectedResult = randomBoolean()
        every { checkNicknameAvailabilityUseCase.invoke(nickname) } returns expectedResult

        // when & then
        mvc.perform(
            get("/api/v1/users/nicknames/availability")
                .queryParam("nickname", nickname),
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$.nickname").value(nickname))
            .andExpect(jsonPath("$.availability").value(expectedResult))
        verify { checkNicknameAvailabilityUseCase.invoke(nickname) }
    }
}
