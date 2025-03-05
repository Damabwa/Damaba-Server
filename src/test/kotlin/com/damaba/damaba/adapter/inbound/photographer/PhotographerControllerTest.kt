package com.damaba.damaba.adapter.inbound.photographer

import com.damaba.damaba.adapter.inbound.photographer.dto.RegisterPhotographerRequest
import com.damaba.damaba.adapter.inbound.photographer.dto.UpdateMyPhotographerProfileRequest
import com.damaba.damaba.application.port.inbound.photographer.ExistsPhotographerNicknameUseCase
import com.damaba.damaba.application.port.inbound.photographer.GetPhotographerUseCase
import com.damaba.damaba.application.port.inbound.photographer.RegisterPhotographerUseCase
import com.damaba.damaba.application.port.inbound.photographer.SavePhotographerUseCase
import com.damaba.damaba.application.port.inbound.photographer.UnsavePhotographerUseCase
import com.damaba.damaba.application.port.inbound.photographer.UpdatePhotographerProfileUseCase
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
import com.damaba.damaba.util.fixture.UserFixture.createUser
import com.damaba.damaba.util.withAuthUser
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
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
    private val existsPhotographerNicknameUseCase: ExistsPhotographerNicknameUseCase,
    private val registerPhotographerUseCase: RegisterPhotographerUseCase,
    private val updatePhotographerProfileUseCase: UpdatePhotographerProfileUseCase,
    private val savePhotographerUseCase: SavePhotographerUseCase,
    private val unsavePhotographerUseCase: UnsavePhotographerUseCase,
) {
    @TestConfiguration
    class TestBeanSetUp {
        @Bean
        fun getPhotographerUseCase(): GetPhotographerUseCase = mockk()

        @Bean
        fun existsPhotographerNicknameUseCase(): ExistsPhotographerNicknameUseCase = mockk()

        @Bean
        fun registerPhotographerUseCase(): RegisterPhotographerUseCase = mockk()

        @Bean
        fun updatePhotographerProfileUseCase(): UpdatePhotographerProfileUseCase = mockk()

        @Bean
        fun savePhotographerUseCase(): SavePhotographerUseCase = mockk()

        @Bean
        fun unsavePhotographerUseCase(): UnsavePhotographerUseCase = mockk()
    }

    @Test
    fun `id가 주어지고, 주어진 id와 일치하는 사진작가를 조회한다`() {
        // given
        val id = randomLong()
        val expectedResult = createPhotographer(id = id)
        every { getPhotographerUseCase.getPhotographer(id) } returns expectedResult

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
            existsPhotographerNicknameUseCase.existsNickname(
                ExistsPhotographerNicknameUseCase.Query(nickname),
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
            existsPhotographerNicknameUseCase.existsNickname(
                ExistsPhotographerNicknameUseCase.Query(nickname),
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
            post("/api/v1/photographers/me/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
                .withAuthUser(createUser(id = userId)),
        ).andExpect(status().isOk)
        verify { registerPhotographerUseCase.register(request.toCommand(userId)) }
    }

    @Test
    fun `사진작가를 저장한다`() {
        // given
        val reqUser = createUser(id = randomLong())
        val photographerId = randomLong()
        val command = SavePhotographerUseCase.Command(reqUserId = reqUser.id, photographerId = photographerId)
        every { savePhotographerUseCase.savePhotographer(command) } just runs

        // when & then
        mvc.perform(
            post("/api/v1/photographers/$photographerId/save")
                .withAuthUser(reqUser),
        ).andExpect(status().isNoContent)
        verify { savePhotographerUseCase.savePhotographer(command) }
    }

    @Test
    fun `내 작가 프로필을 수정한다`() {
        // given
        val photographerId = randomLong()
        val expectedResult = createPhotographer(id = photographerId)
        val requestBody = UpdateMyPhotographerProfileRequest(
            nickname = randomString(len = 10),
            profileImage = createImageRequest(name = "newImage", url = "https://new-image.jpg"),
            mainPhotographyTypes = setOf(PhotographyType.SNAP),
            activeRegions = generateRandomSet(maxSize = 3) { createRegionRequest() },
        )
        every {
            updatePhotographerProfileUseCase.updatePhotographerProfile(requestBody.toCommand(photographerId))
        } returns expectedResult

        // when & then
        mvc.perform(
            put("/api/v1/photographers/me/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(requestBody))
                .withAuthUser(createUser(id = photographerId)),
        ).andExpect(status().isOk)
        verify { updatePhotographerProfileUseCase.updatePhotographerProfile(requestBody.toCommand(photographerId)) }
    }

    @Test
    fun `사진작가 저장을 해제한다`() {
        // given
        val reqUser = createUser(id = randomLong())
        val photographerId = randomLong()
        val command = UnsavePhotographerUseCase.Command(reqUserId = reqUser.id, photographerId = photographerId)
        every { unsavePhotographerUseCase.unsavePhotographer(command) } just runs

        // when & then
        mvc.perform(
            delete("/api/v1/photographers/$photographerId/unsave")
                .withAuthUser(reqUser),
        ).andExpect(status().isNoContent)
        verify { unsavePhotographerUseCase.unsavePhotographer(command) }
    }
}
