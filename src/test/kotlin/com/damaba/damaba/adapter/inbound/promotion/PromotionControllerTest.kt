package com.damaba.damaba.adapter.inbound.promotion

import com.damaba.damaba.adapter.inbound.common.dto.AddressRequest
import com.damaba.damaba.adapter.inbound.common.dto.ImageRequest
import com.damaba.damaba.adapter.inbound.promotion.dto.PostPromotionRequest
import com.damaba.damaba.adapter.inbound.region.dto.RegionRequest
import com.damaba.damaba.application.port.inbound.promotion.FindPromotionsUseCase
import com.damaba.damaba.application.port.inbound.promotion.GetPromotionDetailUseCase
import com.damaba.damaba.application.port.inbound.promotion.GetPromotionUseCase
import com.damaba.damaba.application.port.inbound.promotion.PostPromotionUseCase
import com.damaba.damaba.application.port.inbound.promotion.SavePromotionUseCase
import com.damaba.damaba.application.port.inbound.promotion.UnsavePromotionUseCase
import com.damaba.damaba.config.ControllerTestConfig
import com.damaba.damaba.domain.common.Pagination
import com.damaba.damaba.domain.common.PhotographyType
import com.damaba.damaba.domain.promotion.constant.PromotionProgressStatus
import com.damaba.damaba.domain.promotion.constant.PromotionSortType
import com.damaba.damaba.domain.promotion.constant.PromotionType
import com.damaba.damaba.domain.region.RegionFilterCondition
import com.damaba.damaba.util.RandomTestUtils.Companion.generateRandomList
import com.damaba.damaba.util.RandomTestUtils.Companion.generateRandomSet
import com.damaba.damaba.util.RandomTestUtils.Companion.randomInt
import com.damaba.damaba.util.RandomTestUtils.Companion.randomLocalDate
import com.damaba.damaba.util.RandomTestUtils.Companion.randomLong
import com.damaba.damaba.util.RandomTestUtils.Companion.randomString
import com.damaba.damaba.util.fixture.PromotionFixture.createPromotion
import com.damaba.damaba.util.fixture.PromotionFixture.createPromotionDetail
import com.damaba.damaba.util.fixture.SecurityFixture.createAuthenticationToken
import com.damaba.damaba.util.fixture.UserFixture.createUser
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
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
    private val getPromotionDetailUseCase: GetPromotionDetailUseCase,
    private val findPromotionsUseCase: FindPromotionsUseCase,
    private val postPromotionUseCase: PostPromotionUseCase,
    private val savePromotionUseCase: SavePromotionUseCase,
    private val unsavePromotionUseCase: UnsavePromotionUseCase,
) {
    @TestConfiguration
    class MockBeanSetUp {
        @Bean
        fun getPromotionUseCase(): GetPromotionUseCase = mockk()

        @Bean
        fun getPromotionDetailUseCase(): GetPromotionDetailUseCase = mockk()

        @Bean
        fun findPromotionsUseCase(): FindPromotionsUseCase = mockk()

        @Bean
        fun postPromotionUseCase(): PostPromotionUseCase = mockk()

        @Bean
        fun savePromotionUseCase(): SavePromotionUseCase = mockk()

        @Bean
        fun unsavePromotionUseCase(): UnsavePromotionUseCase = mockk()
    }

    @Test
    fun `프로모션 id가 주어지고, 프로모션을 단건 조회한다`() {
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
    fun `프로모션 id가 주어지고, 프로모션을 상세 조회한다`() {
        // given
        val promotionId = randomLong(positive = true)
        val expectedResult = createPromotionDetail(id = promotionId, author = null)
        every {
            getPromotionDetailUseCase.getPromotionDetail(GetPromotionDetailUseCase.Query(null, promotionId))
        } returns expectedResult

        // when & then
        mvc.perform(
            get("/api/v1/promotions/$promotionId/details"),
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(expectedResult.id))
        verify { getPromotionDetailUseCase.getPromotionDetail(GetPromotionDetailUseCase.Query(null, promotionId)) }
    }

    @Test
    fun `필터 조건들이 주어지고, 프로모션 리스트를 조회한다`() {
        // given
        val type = PromotionType.FREE
        val progressStatus = PromotionProgressStatus.ONGOING
        val regions = setOf(RegionFilterCondition("서울", "강남구"), RegionFilterCondition("대전", null))
        val photographyTypes = setOf(PhotographyType.PROFILE, PhotographyType.SELF)
        val sortType = PromotionSortType.LATEST
        val page = 1
        val pageSize = randomInt(min = 5, max = 15)
        val expectedResult = Pagination(
            items = generateRandomList(maxSize = pageSize) { createPromotion() },
            page = page,
            pageSize = pageSize,
            totalPage = randomInt(min = 1, max = 10),
        )
        every {
            findPromotionsUseCase.findPromotions(
                FindPromotionsUseCase.Query(type, progressStatus, regions, photographyTypes, sortType, page, pageSize),
            )
        } returns expectedResult

        // when & then
        val requestBuilder = get("/api/v1/promotions")
        regions.forEach { region ->
            requestBuilder.param(
                "regions",
                if (region.name == null) region.category else "${region.category} ${region.name}",
            )
        }
        photographyTypes.forEach { photographyType -> requestBuilder.param("photographyTypes", photographyType.name) }
        requestBuilder
            .param("type", type.name)
            .param("progressStatus", progressStatus.name)
            .param("sortType", sortType.name)
            .param("page", page.toString())
            .param("pageSize", pageSize.toString())
        mvc.perform(requestBuilder)
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.items", hasSize<Int>(expectedResult.items.size)))
            .andExpect(jsonPath("$.page").value(expectedResult.page))
            .andExpect(jsonPath("$.pageSize").value(expectedResult.pageSize))
            .andExpect(jsonPath("$.totalPage").value(expectedResult.totalPage))
        verify {
            findPromotionsUseCase.findPromotions(
                FindPromotionsUseCase.Query(type, progressStatus, regions, photographyTypes, sortType, page, pageSize),
            )
        }
    }

    @Test
    fun `주어진 필터 조건 없이, 프로모션 리스트를 조회한다`() {
        // given
        val sortType = PromotionSortType.LATEST
        val page = 1
        val pageSize = randomInt(min = 5, max = 15)
        val expectedResult = Pagination(
            items = generateRandomList(maxSize = pageSize) { createPromotion() },
            page = page,
            pageSize = pageSize,
            totalPage = randomInt(min = 1, max = 10),
        )
        every {
            findPromotionsUseCase.findPromotions(
                FindPromotionsUseCase.Query(
                    type = null,
                    progressStatus = null,
                    regions = emptySet(),
                    photographyTypes = emptySet(),
                    sortType = sortType,
                    page = page,
                    pageSize = pageSize,
                ),
            )
        } returns expectedResult

        // when & then
        mvc.perform(
            get("/api/v1/promotions")
                .param("sortType", sortType.name)
                .param("page", page.toString())
                .param("pageSize", pageSize.toString()),
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$.items", hasSize<Int>(expectedResult.items.size)))
            .andExpect(jsonPath("$.page").value(expectedResult.page))
            .andExpect(jsonPath("$.pageSize").value(expectedResult.pageSize))
            .andExpect(jsonPath("$.totalPage").value(expectedResult.totalPage))
        verify {
            findPromotionsUseCase.findPromotions(
                FindPromotionsUseCase.Query(
                    type = null,
                    progressStatus = null,
                    regions = emptySet(),
                    photographyTypes = emptySet(),
                    sortType = sortType,
                    page = page,
                    pageSize = pageSize,
                ),
            )
        }
    }

    @Test
    fun `잘못된 형식의 지역 필터링 데이터가 주어지고, 프로모션 리스트를 조회하면, validation exception이 발생한다`() {
        // given
        val sortType = PromotionSortType.LATEST
        val page = 1
        val pageSize = randomInt(min = 5, max = 15)

        // when & then
        mvc.perform(
            get("/api/v1/promotions")
                .param("regions", "경기 수원 원천")
                .param("sortType", sortType.name)
                .param("page", page.toString())
                .param("pageSize", pageSize.toString()),
        ).andExpect(status().isUnprocessableEntity)
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

    @Test
    fun `프로모션을 저장한다`() {
        // given
        val requester = createUser()
        val promotionId = randomLong()
        val command = SavePromotionUseCase.Command(userId = requester.id, promotionId = promotionId)
        every { savePromotionUseCase.savePromotion(command) } just runs

        // when & then
        mvc.perform(
            post("/api/v1/promotions/$promotionId/save")
                .with(authentication(createAuthenticationToken(requester))),
        ).andExpect(status().isNoContent)
        verify { savePromotionUseCase.savePromotion(command) }
    }

    @Test
    fun `프로모션 저장을 해제한다`() {
        // given
        val requester = createUser()
        val promotionId = randomLong()
        val command = UnsavePromotionUseCase.Command(userId = requester.id, promotionId = promotionId)
        every { unsavePromotionUseCase.unsavePromotion(command) } just runs

        // when & then
        mvc.perform(
            delete("/api/v1/promotions/$promotionId/unsave")
                .with(authentication(createAuthenticationToken(requester))),
        ).andExpect(status().isNoContent)
        verify { unsavePromotionUseCase.unsavePromotion(command) }
    }
}
