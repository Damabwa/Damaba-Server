package com.damaba.damaba.adapter.inbound.promotion

import com.damaba.damaba.adapter.inbound.common.dto.AddressRequest
import com.damaba.damaba.adapter.inbound.common.dto.ImageRequest
import com.damaba.damaba.adapter.inbound.promotion.dto.PostPromotionRequest
import com.damaba.damaba.adapter.inbound.region.dto.RegionRequest
import com.damaba.damaba.application.port.inbound.promotion.FindPromotionsUseCase
import com.damaba.damaba.application.port.inbound.promotion.GetPromotionUseCase
import com.damaba.damaba.application.port.inbound.promotion.PostPromotionUseCase
import com.damaba.damaba.config.ControllerTestConfig
import com.damaba.damaba.domain.common.Pagination
import com.damaba.damaba.domain.common.PhotographyType
import com.damaba.damaba.domain.promotion.constant.PromotionType
import com.damaba.damaba.util.RandomTestUtils.Companion.generateRandomList
import com.damaba.damaba.util.RandomTestUtils.Companion.generateRandomSet
import com.damaba.damaba.util.RandomTestUtils.Companion.randomInt
import com.damaba.damaba.util.RandomTestUtils.Companion.randomLocalDate
import com.damaba.damaba.util.RandomTestUtils.Companion.randomLong
import com.damaba.damaba.util.RandomTestUtils.Companion.randomString
import com.damaba.damaba.util.fixture.PromotionFixture.createPromotion
import com.damaba.damaba.util.fixture.SecurityFixture.createAuthenticationToken
import com.damaba.damaba.util.fixture.UserFixture.createUser
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.hamcrest.Matchers.hasSize
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import kotlin.test.Test

@ActiveProfiles("test")
@Import(ControllerTestConfig::class)
@WebMvcTest(PromotionController::class)
class PromotionControllerTest @Autowired constructor(
    private val mvc: MockMvc,
    private val mapper: ObjectMapper,
    private val getPromotionUseCase: GetPromotionUseCase,
    private val findPromotionsUseCase: FindPromotionsUseCase,
    private val postPromotionUseCase: PostPromotionUseCase,
) {
    @TestConfiguration
    class MockBeanSetUp {
        @Bean
        fun getPromotionUseCase(): GetPromotionUseCase = mockk()

        @Bean
        fun findPromotionsUseCase(): FindPromotionsUseCase = mockk()

        @Bean
        fun postPromotionUseCase(): PostPromotionUseCase = mockk()
    }

    @Test
    fun `프로모션 id가 주어지고, 프로모션을 상세 조회한다`() {
        // given
        val promotionId = randomLong(positive = true)
        val expectedResult = createPromotion(id = promotionId)
        every { getPromotionUseCase.getPromotion(promotionId) } returns expectedResult

        // when & then
        mvc.perform(
            get("/api/v1/promotions/$promotionId"),
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(expectedResult.id))
        verify { getPromotionUseCase.getPromotion(promotionId) }
    }

    @Test
    fun `프로모션 리스트를 조회한다`() {
        // given
        val page = 1
        val pageSize = randomInt(min = 5, max = 15)
        val totalPage = 3
        val expectedResult = Pagination(
            items = generateRandomList(maxSize = pageSize) { createPromotion() },
            page = page,
            pageSize = pageSize,
            totalPage = totalPage,
        )
        every {
            findPromotionsUseCase.findPromotions(FindPromotionsUseCase.Query(page, pageSize))
        } returns expectedResult

        // when & then
        mvc.perform(
            get("/api/v1/promotions")
                .param("page", page.toString())
                .param("pageSize", pageSize.toString()),
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$.items", hasSize<Int>(expectedResult.items.size)))
            .andExpect(jsonPath("$.page").value(expectedResult.page))
            .andExpect(jsonPath("$.pageSize").value(expectedResult.pageSize))
            .andExpect(jsonPath("$.totalPage").value(expectedResult.totalPage))
        verify { findPromotionsUseCase.findPromotions(FindPromotionsUseCase.Query(page, pageSize)) }
    }

    @Test
    fun `등록한 프로모션 정보들이 주어지고, 주어진 정보로 신규 프로모션을 등록한다`() {
        // given
        val requestUser = createUser()
        val request = PostPromotionRequest(
            promotionType = PromotionType.FREE,
            title = randomString(len = 10),
            content = randomString(),
            address = AddressRequest(
                sido = randomString(),
                sigungu = randomString(),
                roadAddress = randomString(),
                jibunAddress = randomString(),
            ),
            externalLink = randomString(),
            startedAt = randomLocalDate(),
            endedAt = randomLocalDate(),
            photographyTypes = setOf(PhotographyType.SNAP),
            images = generateRandomList(maxSize = 3) { ImageRequest(randomString(), randomString()) },
            activeRegions = generateRandomSet(maxSize = 3) { RegionRequest(randomString(), randomString()) },
            hashtags = generateRandomSet(maxSize = 3) { randomString() },
        )
        val expectedResult = createPromotion(authorId = requestUser.id)
        every { postPromotionUseCase.postPromotion(any(PostPromotionUseCase.Command::class)) } returns expectedResult

        // when & then
        mvc.perform(
            post("/api/v1/promotions")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(request))
                .with(authentication(createAuthenticationToken(requestUser))),
        ).andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(expectedResult.id))
            .andExpect(jsonPath("$.authorId").value(requestUser.id))
        verify { postPromotionUseCase.postPromotion(any(PostPromotionUseCase.Command::class)) }
    }
}
