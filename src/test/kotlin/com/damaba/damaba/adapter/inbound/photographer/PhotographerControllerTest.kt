package com.damaba.damaba.adapter.inbound.photographer

import com.damaba.damaba.adapter.inbound.photographer.dto.RegisterPhotographerRequest
import com.damaba.damaba.application.port.inbound.photographer.CheckPhotographerNicknameExistenceUseCase
import com.damaba.damaba.application.port.inbound.photographer.GetPhotographerUseCase
import com.damaba.damaba.application.port.inbound.photographer.RegisterPhotographerUseCase
import com.damaba.damaba.config.ControllerTestConfig
import com.damaba.damaba.domain.common.PhotographyType
import com.damaba.damaba.domain.user.constant.Gender
import com.damaba.damaba.util.RandomTestUtils.Companion.generateRandomSet
import com.damaba.damaba.util.RandomTestUtils.Companion.randomBoolean
import com.damaba.damaba.util.RandomTestUtils.Companion.randomLong
import com.damaba.damaba.util.RandomTestUtils.Companion.randomString
import com.damaba.damaba.util.fixture.FileFixture.createImageRequest
import com.damaba.damaba.util.fixture.PhotographerFixture.createPhotographer
import com.damaba.damaba.util.fixture.RegionFixture.createRegionRequest
import com.damaba.damaba.util.fixture.SecurityFixture.createAuthenticationToken
import com.damaba.damaba.util.fixture.UserFixture.createUser
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
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import kotlin.test.Test

@ActiveProfiles("test")
@Import(ControllerTestConfig::class)
@WebMvcTest(PhotographerController::class)
class PhotographerControllerTest @Autowired constructor(
    private val mvc: MockMvc,
    private val mapper: ObjectMapper,
    private val getPhotographerUseCase: GetPhotographerUseCase,
    private val checkPhotographerNicknameExistenceUseCase: CheckPhotographerNicknameExistenceUseCase,
    private val registerPhotographerUseCase: RegisterPhotographerUseCase,
) {
    @TestConfiguration
    class TestBeanSetUp {
        @Bean
        fun getPhotographerUseCase(): GetPhotographerUseCase = mockk()

        @Bean
        fun checkPhotographerNicknameExistenceUseCase(): CheckPhotographerNicknameExistenceUseCase = mockk()

        @Bean
        fun registerPhotographerUseCase(): RegisterPhotographerUseCase = mockk()
    }

    @Test
    fun `id가 주어지고, 주어진 id와 일치하는 사진작가를 조회한다`() {
        // given
        val id = randomLong()
        val expectedResult = createPhotographer(id = id)
        every { getPhotographerUseCase.getById(id) } returns expectedResult

        // when & then
        mvc.perform(
            get("/api/v1/photographers/{photographerId}", id),
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(expectedResult.id))
    }

    @Test
    fun `닉네임이 주어지고, 닉네임의 사용 여부를 조회한다`() {
        // given
        val nickname = randomString()
        val expectedResult = randomBoolean()
        every {
            checkPhotographerNicknameExistenceUseCase.doesNicknameExist(
                CheckPhotographerNicknameExistenceUseCase.Query(nickname),
            )
        } returns expectedResult

        // when & then
        mvc.perform(
            get("/api/v1/photographers/nicknames/existence")
                .param("nickname", nickname),
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$.nickname").value(nickname))
            .andExpect(jsonPath("$.exists").value(expectedResult))
        verify {
            checkPhotographerNicknameExistenceUseCase.doesNicknameExist(
                CheckPhotographerNicknameExistenceUseCase.Query(nickname),
            )
        }
    }

    @Test
    fun `사진작가 등록 정보가 주어지고, 사진작가로 등록한다`() {
        // given
        val userId = randomLong()
        val request = RegisterPhotographerRequest(
            nickname = randomString(len = 10),
            gender = Gender.FEMALE,
            instagramId = randomString(len = 15),
            profileImage = createImageRequest(),
            mainPhotographyTypes = setOf(PhotographyType.PROFILE, PhotographyType.SELF),
            activeRegions = generateRandomSet(maxSize = 3) { createRegionRequest() },
        )
        val expectedResult = createPhotographer(id = userId)
        every { registerPhotographerUseCase.register(request.toCommand(userId)) } returns expectedResult

        // when & then
        mvc.perform(
            put("/api/v1/photographers/me/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
                .with(authentication(createAuthenticationToken(createUser(id = userId)))),
        ).andExpect(status().isOk)
        verify { registerPhotographerUseCase.register(request.toCommand(userId)) }
    }
}
