package com.damaba.damaba.adapter.inbound.promotion

import com.damaba.damaba.adapter.inbound.promotion.dto.PostPromotionRequest
import com.damaba.damaba.application.port.inbound.promotion.GetPromotionDetailUseCase
import com.damaba.damaba.application.port.inbound.promotion.PostPromotionUseCase
import com.damaba.damaba.config.ControllerTestConfig
import com.damaba.damaba.domain.promotion.constant.EventType
import com.damaba.damaba.domain.promotion.constant.PromotionType
import com.damaba.damaba.util.RandomTestUtils.Companion.generateRandomList
import com.damaba.damaba.util.RandomTestUtils.Companion.generateRandomSet
import com.damaba.damaba.util.RandomTestUtils.Companion.randomLocalDate
import com.damaba.damaba.util.RandomTestUtils.Companion.randomLong
import com.damaba.damaba.util.RandomTestUtils.Companion.randomString
import com.damaba.damaba.util.TestFixture.createAuthenticationToken
import com.damaba.damaba.util.TestFixture.createMockMultipartFile
import com.damaba.damaba.util.TestFixture.createPromotion
import com.damaba.damaba.util.TestFixture.createUser
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import kotlin.test.Test

@ActiveProfiles("test")
@Import(ControllerTestConfig::class)
@WebMvcTest(PromotionController::class)
class PromotionControllerTest @Autowired constructor(
    private val mvc: MockMvc,
    private val getPromotionDetailUseCase: GetPromotionDetailUseCase,
    private val postPromotionUseCase: PostPromotionUseCase,
) {
    @TestConfiguration
    class MockBeanSetUp {
        @Bean fun getPromotionDetailUseCase(): GetPromotionDetailUseCase = mockk()

        @Bean fun postPromotionUseCase(): PostPromotionUseCase = mockk()
    }

    @Test
    fun `프로모션 id가 주어지고, 프로모션을 상세 조회한다`() {
        // given
        val promotionId = randomLong()
        val expectedResult = createPromotion(id = promotionId)
        every { getPromotionDetailUseCase.getPromotionDetail(promotionId) } returns expectedResult

        // when & then
        mvc.perform(
            get("/api/v1/promotions/$promotionId"),
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(expectedResult.id))
        verify { getPromotionDetailUseCase.getPromotionDetail(promotionId) }
    }

    @Test
    fun `등록한 프로모션 정보들이 주어지고, 주어진 정보로 신규 프로모션을 등록한다`() {
        // given
        val requestUser = createUser()
        val request = PostPromotionRequest(
            type = PromotionType.EVENT,
            eventType = EventType.FREE,
            title = randomString(len = 10),
            content = randomString(),
            sido = randomString(),
            sigungu = randomString(),
            roadAddress = randomString(),
            jibunAddress = randomString(),
            externalLink = randomString(),
            startedAt = randomLocalDate(),
            endedAt = randomLocalDate(),
            photographerName = randomString(),
            photographerInstagramId = randomString(),
            images = generateRandomList(maxSize = 3) { createMockMultipartFile() },
            activeRegions = setOf("서울 강남구", "경기 성남시 분당구", "대전 서구"),
            hashtags = generateRandomSet(maxSize = 3) { randomString() },
        )
        val expectedResult = createPromotion(authorId = requestUser.id)
        every { postPromotionUseCase.postPromotion(any(PostPromotionUseCase.Command::class)) } returns expectedResult

        // when & then
        val requesterBuilder = multipart("/api/v1/promotions")
        request.images.forEach { image -> requesterBuilder.file("images", image.bytes) }
        request.activeRegions.forEach { region -> requesterBuilder.param("activeRegions", region) }
        request.hashtags.forEach { hashtag -> requesterBuilder.param("hashtags", hashtag) }
        mvc.perform(
            requesterBuilder
                .param("type", request.type.toString())
                .param("eventType", request.eventType.toString())
                .param("title", request.title)
                .param("content", request.content)
                .param("sido", request.sido)
                .param("sigungu", request.sigungu)
                .param("roadAddress", request.roadAddress)
                .param("jibunAddress", request.jibunAddress)
                .with(authentication(createAuthenticationToken(requestUser))),
        ).andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(expectedResult.id))
            .andExpect(jsonPath("$.authorId").value(requestUser.id))
        verify { postPromotionUseCase.postPromotion(any(PostPromotionUseCase.Command::class)) }
    }
}
