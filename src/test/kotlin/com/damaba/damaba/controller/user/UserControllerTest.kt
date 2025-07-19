package com.damaba.damaba.controller.user

import com.damaba.damaba.application.photographer.PhotographerService
import com.damaba.damaba.application.user.ExistsUserNicknameQuery
import com.damaba.damaba.application.user.UserService
import com.damaba.damaba.config.ControllerTestConfig
import com.damaba.damaba.controller.common.ImageRequest
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
    private val userService: UserService,
    private val photographerService: PhotographerService,
) {
    @TestConfiguration
    class TestBeanSetUp {
        @Bean
        fun userService(): UserService = mockk()

        @Bean
        fun photographerService(): PhotographerService = mockk()
    }

    @Test
    fun `내 정보를 조회하면, 내 정보가 응답된다`() {
        // given
        val userId = randomLong()
        val me = createUser(id = userId)
        every { userService.getUser(userId) } returns me

        // when and then
        mvc.perform(
            get("/api/v1/users/me")
                .withAuthUser(me),
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(me.id))
            .andExpect(jsonPath("$.nickname").value(me.nickname))
        verify { userService.getUser(userId) }
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
        every { userService.updateUserProfile(request.toCommand(requestUser.id)) } returns expectedResult

        // when and then
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
            .andExpect(jsonPath("$.profileImage.name").value(expectedResult.profileImage?.name))
            .andExpect(jsonPath("$.profileImage.url").value(expectedResult.profileImage?.url))
        verify { userService.updateUserProfile(request.toCommand(requestUser.id)) }
    }

    @Test
    fun `닉네임이 주어지고, 주어진 닉네임의 이용가능성을 확인한다`() {
        // given
        val nickname = randomString(len = 7)
        val expectedResult = randomBoolean()
        every { userService.existsNickname(ExistsUserNicknameQuery(nickname)) } returns expectedResult

        // when and then
        mvc.perform(
            get("/api/v1/users/nicknames/existence")
                .queryParam("nickname", nickname),
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$.nickname").value(nickname))
            .andExpect(jsonPath("$.exists").value(expectedResult))
        verify { userService.existsNickname(ExistsUserNicknameQuery(nickname)) }
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
        every { userService.register(request.toCommand(requester.id)) } returns expectedResult

        // when and then
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
        verify { userService.register(request.toCommand(requester.id)) }
    }
}
