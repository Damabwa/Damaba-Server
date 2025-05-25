package com.damaba.damaba.controller.promotion

import com.damaba.damaba.application.promotion.PromotionService
import com.damaba.damaba.application.promotion.dto.DeletePromotionCommand
import com.damaba.damaba.application.promotion.dto.FindPromotionListQuery
import com.damaba.damaba.application.promotion.dto.FindSavedPromotionListQuery
import com.damaba.damaba.application.promotion.dto.GetPromotionDetailQuery
import com.damaba.damaba.application.promotion.dto.PostPromotionCommand
import com.damaba.damaba.application.promotion.dto.SavePromotionCommand
import com.damaba.damaba.application.promotion.dto.UnsavePromotionCommand
import com.damaba.damaba.application.promotion.dto.UpdatePromotionCommand
import com.damaba.damaba.config.ControllerTestConfig
import com.damaba.damaba.controller.common.dto.ImageRequest
import com.damaba.damaba.controller.promotion.dto.PostPromotionRequest
import com.damaba.damaba.controller.promotion.dto.UpdatePromotionRequest
import com.damaba.damaba.controller.region.dto.RegionRequest
import com.damaba.damaba.domain.common.Pagination
import com.damaba.damaba.domain.common.constant.PhotographyType
import com.damaba.damaba.domain.promotion.constant.PromotionProgressStatus
import com.damaba.damaba.domain.promotion.constant.PromotionSortType
import com.damaba.damaba.domain.promotion.constant.PromotionType
import com.damaba.damaba.domain.region.RegionFilterCondition
import com.damaba.damaba.mapper.ImageMapper
import com.damaba.damaba.mapper.RegionMapper
import com.damaba.damaba.util.RandomTestUtils.Companion.generateRandomList
import com.damaba.damaba.util.RandomTestUtils.Companion.generateRandomSet
import com.damaba.damaba.util.RandomTestUtils.Companion.randomInt
import com.damaba.damaba.util.RandomTestUtils.Companion.randomLocalDate
import com.damaba.damaba.util.RandomTestUtils.Companion.randomLong
import com.damaba.damaba.util.RandomTestUtils.Companion.randomString
import com.damaba.damaba.util.RandomTestUtils.Companion.randomUrl
import com.damaba.damaba.util.fixture.FileFixture.createImageRequest
import com.damaba.damaba.util.fixture.PromotionFixture.createPromotion
import com.damaba.damaba.util.fixture.PromotionFixture.createPromotionDetail
import com.damaba.damaba.util.fixture.PromotionFixture.createPromotionListItem
import com.damaba.damaba.util.fixture.RegionFixture.createRegionRequest
import com.damaba.damaba.util.fixture.SecurityFixture.createAuthenticationToken
import com.damaba.damaba.util.fixture.UserFixture.createUser
import com.damaba.damaba.util.withAuthUser
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import kotlin.test.Test

@ActiveProfiles("test")
@Import(ControllerTestConfig::class)
@WebMvcTest(PromotionController::class)
class PromotionControllerTest @Autowired constructor(
    private val mvc: MockMvc,
    private val mapper: ObjectMapper,
    private val promotionService: PromotionService,
) {

    @TestConfiguration
    class MockBeanSetUp {
        @Bean
        fun promotionService(): PromotionService = mockk()
    }

    @Test
    fun `프로모션 id가 주어지고, 프로모션을 단건 조회한다`() {
        // given
        val promotionId = randomLong(positive = true)
        val expectedResult = createPromotion(id = promotionId)
        every { promotionService.getPromotion(promotionId) } returns expectedResult

        // when and then
        mvc.perform(
            get("/api/v1/promotions/$promotionId"),
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(expectedResult.id))
        verify { promotionService.getPromotion(promotionId) }
    }

    @Test
    fun `프로모션 id가 주어지고, 프로모션을 상세 조회한다`() {
        // given
        val promotionId = randomLong(positive = true)
        val expectedResult = createPromotionDetail(id = promotionId, author = null)
        every {
            promotionService.getPromotionDetail(GetPromotionDetailQuery(null, promotionId))
        } returns expectedResult

        // when and then
        mvc.perform(
            get("/api/v1/promotions/$promotionId/details"),
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(expectedResult.id))
        verify { promotionService.getPromotionDetail(GetPromotionDetailQuery(null, promotionId)) }
    }

    @Test
    fun `요청자 정보와 프로모션 id가 주어지고, 프로모션을 상세 조회한다`() {
        // given
        val requestUser = createUser()
        val promotionId = randomLong(positive = true)
        val expectedResult = createPromotionDetail(id = promotionId, author = null)
        every {
            promotionService.getPromotionDetail(GetPromotionDetailQuery(requestUser.id, promotionId))
        } returns expectedResult

        // when and then
        mvc.perform(
            get("/api/v1/promotions/$promotionId/details")
                .withAuthUser(requestUser),
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(expectedResult.id))
        verify {
            promotionService.getPromotionDetail(GetPromotionDetailQuery(requestUser.id, promotionId))
        }
    }

    @Test
    fun `필터 조건들이 주어지고, 프로모션 리스트를 조회한다`() {
        // given
        val reqUser = createUser()
        val type = PromotionType.FREE
        val progressStatus = PromotionProgressStatus.ONGOING
        val regions = setOf(RegionFilterCondition("서울", "강남구"), RegionFilterCondition("대전", null))
        val photographyTypes = setOf(PhotographyType.PROFILE, PhotographyType.SELF)
        val sortType = PromotionSortType.LATEST
        val page = 1
        val pageSize = randomInt(min = 5, max = 15)
        val expectedResult = Pagination(
            items = generateRandomList(maxSize = pageSize) { createPromotionListItem() },
            page = page,
            pageSize = pageSize,
            totalPage = randomInt(min = 1, max = 10),
        )
        every {
            promotionService.findPromotionList(
                FindPromotionListQuery(
                    requestUserId = reqUser.id,
                    type = type,
                    progressStatus = progressStatus,
                    regions = regions,
                    photographyTypes = photographyTypes,
                    sortType = sortType,
                    page = page,
                    pageSize = pageSize,
                ),
            )
        } returns expectedResult

        // when and then
        val requestBuilder = get("/api/v1/promotions/list")
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
            .with(authentication(createAuthenticationToken(reqUser)))
        mvc.perform(requestBuilder)
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.items", hasSize<Int>(expectedResult.items.size)))
            .andExpect(jsonPath("$.page").value(expectedResult.page))
            .andExpect(jsonPath("$.pageSize").value(expectedResult.pageSize))
            .andExpect(jsonPath("$.totalPage").value(expectedResult.totalPage))
        verify {
            promotionService.findPromotionList(
                FindPromotionListQuery(
                    requestUserId = reqUser.id,
                    type = type,
                    progressStatus = progressStatus,
                    regions = regions,
                    photographyTypes = photographyTypes,
                    sortType = sortType,
                    page = page,
                    pageSize = pageSize,
                ),
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
            items = generateRandomList(maxSize = pageSize) { createPromotionListItem() },
            page = page,
            pageSize = pageSize,
            totalPage = randomInt(min = 1, max = 10),
        )
        every {
            promotionService.findPromotionList(
                FindPromotionListQuery(
                    requestUserId = null,
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

        // when and then
        mvc.perform(
            get("/api/v1/promotions/list")
                .param("sortType", sortType.name)
                .param("page", page.toString())
                .param("pageSize", pageSize.toString()),
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$.items", hasSize<Int>(expectedResult.items.size)))
            .andExpect(jsonPath("$.page").value(expectedResult.page))
            .andExpect(jsonPath("$.pageSize").value(expectedResult.pageSize))
            .andExpect(jsonPath("$.totalPage").value(expectedResult.totalPage))
        verify {
            promotionService.findPromotionList(
                FindPromotionListQuery(
                    requestUserId = null,
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

        // when and then
        mvc.perform(
            get("/api/v1/promotions/list")
                .param("regions", "경기 수원 원천")
                .param("sortType", sortType.name)
                .param("page", page.toString())
                .param("pageSize", pageSize.toString()),
        ).andExpect(status().isUnprocessableEntity)
    }

    @Test
    fun `저장된 프로모션 리스트를 조회한다`() {
        // given
        val requestUser = createUser()
        val page = 1
        val pageSize = randomInt(min = 5, max = 15)
        val expectedResult = Pagination(
            items = generateRandomList(maxSize = pageSize) { createPromotionListItem() },
            page = page,
            pageSize = pageSize,
            totalPage = randomInt(min = 1, max = 10),
        )
        every {
            promotionService.findSavedPromotionList(
                FindSavedPromotionListQuery(
                    requestUserId = requestUser.id,
                    page = page,
                    pageSize = pageSize,
                ),
            )
        } returns expectedResult

        // when and then
        mvc.perform(
            get("/api/v1/promotions/saved")
                .param("page", page.toString())
                .param("pageSize", pageSize.toString())
                .with(authentication(createAuthenticationToken(requestUser))),
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$.items", hasSize<Int>(expectedResult.items.size)))
            .andExpect(jsonPath("$.page").value(expectedResult.page))
            .andExpect(jsonPath("$.pageSize").value(expectedResult.pageSize))
            .andExpect(jsonPath("$.totalPage").value(expectedResult.totalPage))
        verify {
            promotionService.findSavedPromotionList(
                FindSavedPromotionListQuery(
                    requestUserId = requestUser.id,
                    page = page,
                    pageSize = pageSize,
                ),
            )
        }
    }

    @Test
    fun `등록한 프로모션 정보들이 주어지고, 주어진 정보로 신규 프로모션을 등록한다`() {
        // given
        val requestUser = createUser()
        val request = PostPromotionRequest(
            promotionType = PromotionType.FREE,
            title = randomString(len = 10),
            content = randomString(),
            externalLink = randomString(),
            startedAt = randomLocalDate(),
            endedAt = randomLocalDate(),
            photographyTypes = setOf(PhotographyType.SNAP),
            images = generateRandomList(maxSize = 3) { ImageRequest(randomString(), randomString()) },
            activeRegions = generateRandomSet(maxSize = 3) { RegionRequest(randomString(), randomString()) },
            hashtags = generateRandomSet(maxSize = 3) { randomString() },
        )
        val expectedResult = createPromotion(authorId = requestUser.id)
        every { promotionService.postPromotion(any(PostPromotionCommand::class)) } returns expectedResult

        // when and then
        mvc.perform(
            post("/api/v1/promotions")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(request))
                .with(authentication(createAuthenticationToken(requestUser))),
        ).andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(expectedResult.id))
            .andExpect(jsonPath("$.authorId").value(requestUser.id))
        verify { promotionService.postPromotion(any(PostPromotionCommand::class)) }
    }

    @Test
    fun `프로모션을 저장한다`() {
        // given
        val requester = createUser()
        val promotionId = randomLong()
        val command = SavePromotionCommand(userId = requester.id, promotionId = promotionId)
        every { promotionService.savePromotion(command) } just runs

        // when and then
        mvc.perform(
            post("/api/v1/promotions/$promotionId/save")
                .with(authentication(createAuthenticationToken(requester))),
        ).andExpect(status().isNoContent)
        verify { promotionService.savePromotion(command) }
    }

    @Test
    fun `프로모션을 수정한다`() {
        // given
        val requestUser = createUser()
        val promotionId = randomLong()
        val request = UpdatePromotionRequest(
            promotionType = PromotionType.FREE,
            title = randomString(),
            content = randomString(),
            externalLink = randomUrl(),
            startedAt = randomLocalDate(),
            endedAt = randomLocalDate(),
            photographyTypes = setOf(PhotographyType.ID_PHOTO),
            images = listOf(createImageRequest()),
            activeRegions = setOf(createRegionRequest()),
            hashtags = setOf(randomString()),
        )
        val command = UpdatePromotionCommand(
            requestUserId = requestUser.id,
            promotionId = promotionId,
            promotionType = request.promotionType,
            title = request.title,
            content = request.content,
            externalLink = request.externalLink,
            startedAt = request.startedAt,
            endedAt = request.endedAt,
            photographyTypes = request.photographyTypes,
            images = request.images.map { ImageMapper.INSTANCE.toImage(it) },
            activeRegions = request.activeRegions.map { RegionMapper.INSTANCE.toRegion(it) }.toSet(),
            hashtags = request.hashtags,
        )
        val expectedResult = createPromotion(
            id = promotionId,
            authorId = requestUser.id,
            promotionType = command.promotionType,
            title = command.title,
            content = command.content,
            externalLink = command.externalLink,
            startedAt = command.startedAt,
            endedAt = command.endedAt,
            photographyTypes = command.photographyTypes,
            images = command.images,
            activeRegions = command.activeRegions,
            hashtags = command.hashtags,
        )
        every { promotionService.updatePromotion(command) } returns expectedResult

        // when and then
        mvc.perform(
            put("/api/v1/promotions/$promotionId")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(request))
                .withAuthUser(requestUser),
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(expectedResult.id))
            .andExpect(jsonPath("$.authorId").value(expectedResult.authorId))
            .andExpect(jsonPath("$.promotionType").value(expectedResult.promotionType.name))
            .andExpect(jsonPath("$.title").value(expectedResult.title))
            .andExpect(jsonPath("$.content").value(expectedResult.content))
            .andExpect(jsonPath("$.externalLink").value(expectedResult.externalLink))
            .andExpect(jsonPath("$.startedAt").value(expectedResult.startedAt.toString()))
            .andExpect(jsonPath("$.endedAt").value(expectedResult.endedAt.toString()))
        verify { promotionService.updatePromotion(command) }
    }

    @Test
    fun `프로모션을 삭제한다`() {
        // given
        val requestUser = createUser(id = 1L)
        val promotionId = randomLong()
        val command = DeletePromotionCommand(requestUser = requestUser, promotionId = promotionId)
        every { promotionService.deletePromotion(command) } just runs

        // when and then
        mvc.perform(
            delete("/api/v1/promotions/$promotionId")
                .withAuthUser(requestUser),
        ).andExpect(status().isNoContent)
        verify { promotionService.deletePromotion(command) }
    }

    @Test
    fun `프로모션 저장을 해제한다`() {
        // given
        val requester = createUser()
        val promotionId = randomLong()
        val command = UnsavePromotionCommand(userId = requester.id, promotionId = promotionId)
        every { promotionService.unsavePromotion(command) } just runs

        // when and then
        mvc.perform(
            delete("/api/v1/promotions/$promotionId/unsave")
                .with(authentication(createAuthenticationToken(requester))),
        ).andExpect(status().isNoContent)
        verify { promotionService.unsavePromotion(command) }
    }
}
