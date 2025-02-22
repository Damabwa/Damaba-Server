package com.damaba.damaba.adapter.inbound.user

import com.damaba.damaba.adapter.inbound.common.dto.ImageRequest
import com.damaba.damaba.adapter.inbound.user.dto.RegisterUserRequest
import com.damaba.damaba.adapter.inbound.user.dto.UpdateMyProfileRequest
import com.damaba.damaba.application.port.inbound.user.ExistsUserNicknameUseCase
import com.damaba.damaba.application.port.inbound.user.GetUserUseCase
import com.damaba.damaba.application.port.inbound.user.RegisterUserUseCase
import com.damaba.damaba.application.port.inbound.user.UpdateUserProfileUseCase
import com.damaba.damaba.config.ControllerTestConfig
import com.damaba.damaba.domain.user.constant.Gender
import com.damaba.damaba.util.RandomTestUtils.Companion.randomBoolean
import com.damaba.damaba.util.RandomTestUtils.Companion.randomLong
import com.damaba.damaba.util.RandomTestUtils.Companion.randomString
import com.damaba.damaba.util.RandomTestUtils.Companion.randomUrl
import com.damaba.damaba.util.fixture.UserFixture.createUser
import com.damaba.damaba.util.withAuthUser
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
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import kotlin.test.Test

@ActiveProfiles("test")
@Import(ControllerTestConfig::class)
@WebMvcTest(controllers = [UserController::class])
class UserControllerTest @Autowired constructor(
    private val mvc: MockMvc,
    private val mapper: ObjectMapper,
    private val getUserUseCase: GetUserUseCase,
    private val existsUserNicknameUseCase: ExistsUserNicknameUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase,
    private val registerUserUseCase: RegisterUserUseCase,
) {
    @TestConfiguration
    class TestBeanSetUp {
        @Bean
        fun getMyInfoUseCase(): GetUserUseCase = mockk()

        @Bean
        fun checkNicknameExistenceUseCase(): ExistsUserNicknameUseCase = mockk()

        @Bean
        fun updateMyInfoUseCase(): UpdateUserProfileUseCase = mockk()

        @Bean
        fun registerUserUseCase(): RegisterUserUseCase = mockk()
    }

    @Test
    fun `내 정보를 조회하면, 내 정보가 응답된다`() {
        // given
        val userId = randomLong()
        val me = createUser(id = userId)
        every { getUserUseCase.getUser(userId) } returns me

        // when & then
        mvc.perform(
            get("/api/v1/users/me")
                .withAuthUser(me),
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(me.id))
            .andExpect(jsonPath("$.nickname").value(me.nickname))
        verify { getUserUseCase.getUser(userId) }
    }

    @Test
    fun `수정할 유저 정보가 주어지고, 유저 정보를 수정하면, 수정된 유저 정보가 응답된다`() {
        // given
        val requestUser = createUser()
        val request = UpdateMyProfileRequest(
            nickname = randomString(len = 7),
            instagramId = randomString(),
            profileImage = ImageRequest(randomString(), randomUrl()),
        )
        val expectedResult = createUser(
            id = requestUser.id,
            nickname = request.nickname,
            instagramId = request.instagramId,
        )
        every { updateUserProfileUseCase.updateUserProfile(request.toCommand(requestUser.id)) } returns expectedResult

        // when & then
        mvc.perform(
            put("/api/v1/users/me/profile")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(request))
                .withAuthUser(requestUser),
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(requestUser.id))
            .andExpect(jsonPath("$.nickname").value(expectedResult.nickname))
            .andExpect(jsonPath("$.gender").value(expectedResult.gender.toString()))
            .andExpect(jsonPath("$.instagramId").value(expectedResult.instagramId))
            .andExpect(jsonPath("$.profileImage.name").value(expectedResult.profileImage.name))
            .andExpect(jsonPath("$.profileImage.url").value(expectedResult.profileImage.url))
        verify { updateUserProfileUseCase.updateUserProfile(request.toCommand(requestUser.id)) }
    }

    @Test
    fun `닉네임이 주어지고, 주어진 닉네임의 이용가능성을 확인한다`() {
        // given
        val nickname = randomString(len = 7)
        val expectedResult = randomBoolean()
        every {
            existsUserNicknameUseCase.existsNickname(ExistsUserNicknameUseCase.Query(nickname))
        } returns expectedResult

        // when & then
        mvc.perform(
            get("/api/v1/users/nicknames/existence")
                .queryParam("nickname", nickname),
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$.nickname").value(nickname))
            .andExpect(jsonPath("$.exists").value(expectedResult))
        verify { existsUserNicknameUseCase.existsNickname(ExistsUserNicknameUseCase.Query(nickname)) }
    }

    @Test
    fun `유저 등록 정보가 주어지고, 주어진 등록 정보로 유저를 갱신한다`() {
        // given
        val requester = createUser()
        val request = RegisterUserRequest(
            nickname = randomString(len = 7),
            gender = Gender.FEMALE,
            instagramId = null,
        )
        val expectedResult = createUser(
            id = requester.id,
            nickname = request.nickname,
            gender = request.gender,
            instagramId = request.instagramId,
        )
        every { registerUserUseCase.register(request.toCommand(requester.id)) } returns expectedResult

        // when & then
        mvc.perform(
            post("/api/v1/users/me/registration")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(request))
                .withAuthUser(requester),
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(requester.id))
            .andExpect(jsonPath("$.nickname").value(expectedResult.nickname))
            .andExpect(jsonPath("$.gender").value(expectedResult.gender.toString()))
            .andExpect(jsonPath("$.instagramId").value(expectedResult.instagramId))
        verify { registerUserUseCase.register(request.toCommand(requester.id)) }
    }
}
