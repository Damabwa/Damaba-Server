package com.damaba.damaba.controller.photographer

import com.damaba.damaba.application.photographer.PhotographerService
import com.damaba.damaba.application.photographer.dto.ExistsPhotographerNicknameQuery
import com.damaba.damaba.application.photographer.dto.FindPhotographerListQuery
import com.damaba.damaba.application.photographer.dto.FindSavedPhotographerListQuery
import com.damaba.damaba.application.photographer.dto.SavePhotographerCommand
import com.damaba.damaba.application.photographer.dto.UnsavePhotographerCommand
import com.damaba.damaba.config.ControllerTestConfig
import com.damaba.damaba.controller.photographer.dto.RegisterPhotographerRequest
import com.damaba.damaba.controller.photographer.dto.UpdateMyPhotographerPageRequest
import com.damaba.damaba.controller.photographer.dto.UpdateMyPhotographerProfileRequest
import com.damaba.damaba.domain.common.Pagination
import com.damaba.damaba.domain.common.constant.PhotographyType
import com.damaba.damaba.domain.photographer.constant.PhotographerSortType
import com.damaba.damaba.domain.region.RegionFilterCondition
import com.damaba.damaba.domain.user.constant.Gender
import com.damaba.damaba.util.RandomTestUtils.Companion.generateRandomList
import com.damaba.damaba.util.RandomTestUtils.Companion.generateRandomSet
import com.damaba.damaba.util.RandomTestUtils.Companion.randomBoolean
import com.damaba.damaba.util.RandomTestUtils.Companion.randomInt
import com.damaba.damaba.util.RandomTestUtils.Companion.randomLong
import com.damaba.damaba.util.RandomTestUtils.Companion.randomString
import com.damaba.damaba.util.fixture.FileFixture.createImage
import com.damaba.damaba.util.fixture.FileFixture.createImageRequest
import com.damaba.damaba.util.fixture.PhotographerFixture.createPhotographer
import com.damaba.damaba.util.fixture.PhotographerFixture.createPhotographerListItem
import com.damaba.damaba.util.fixture.RegionFixture.createRegionRequest
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
    private val photographerService: PhotographerService,
) {
    @TestConfiguration
    class TestBeanSetUp {
        @Bean
        fun photographerService(): PhotographerService = mockk()
    }

    @Test
    fun `id가 주어지고, 주어진 id와 일치하는 사진작가를 조회한다`() {
        // given
        val id = randomLong()
        val expectedResult = createPhotographer(id = id)
        every { photographerService.getPhotographer(id) } returns expectedResult

        // when and then
        mvc.perform(
            get("/api/v1/photographers/{photographerId}", id),
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(expectedResult.id))
    }

    @Test
    fun `필터링 조건들이 주어지고, 사진작가 리스트를 조회한다`() {
        // given
        val requestUser = createUser()
        val regions = setOf(RegionFilterCondition("서울", "강남구"), RegionFilterCondition("대전", null))
        val photographyTypes = setOf(PhotographyType.PROFILE, PhotographyType.SELF)
        val searchKeyword = randomString()
        val sort = PhotographerSortType.LATEST
        val page = 1
        val pageSize = randomInt(min = 5, max = 15)
        val expectedResult = Pagination(
            items = generateRandomList(maxSize = pageSize) { createPhotographerListItem(profileImage = createImage()) },
            page = page,
            pageSize = pageSize,
            totalPage = 10,
        )
        every {
            photographerService.findPhotographerList(
                FindPhotographerListQuery(
                    requestUserId = requestUser.id,
                    regions = regions,
                    photographyTypes = photographyTypes,
                    searchKeyword = searchKeyword,
                    sort = sort,
                    page = page,
                    pageSize = pageSize,
                ),
            )
        } returns expectedResult

        // when and then
        val requestBuilder = get("/api/v1/photographers/list")
        regions.forEach { region ->
            requestBuilder.param(
                "regions",
                if (region.name == null) region.category else "${region.category} ${region.name}",
            )
        }
        photographyTypes.forEach { photographyType -> requestBuilder.param("photographyTypes", photographyType.name) }
        requestBuilder
            .param("searchKeyword", searchKeyword)
            .param("sort", sort.name)
            .param("page", page.toString())
            .param("pageSize", pageSize.toString())
            .withAuthUser(requestUser)
        mvc.perform(requestBuilder)
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.items", hasSize<Int>(expectedResult.items.size)))
            .andExpect(jsonPath("$.page").value(expectedResult.page))
            .andExpect(jsonPath("$.pageSize").value(expectedResult.pageSize))
            .andExpect(jsonPath("$.totalPage").value(expectedResult.totalPage))
        verify {
            photographerService.findPhotographerList(
                FindPhotographerListQuery(
                    requestUserId = requestUser.id,
                    regions = regions,
                    photographyTypes = photographyTypes,
                    searchKeyword = searchKeyword,
                    sort = sort,
                    page = page,
                    pageSize = pageSize,
                ),
            )
        }
    }

    @Test
    fun `주어진 필터링 조건 없이, 사진작가 리스트를 조회한다`() {
        // given
        val sort = PhotographerSortType.LATEST
        val page = 1
        val pageSize = randomInt(min = 5, max = 15)
        val expectedResult = Pagination(
            items = generateRandomList(maxSize = pageSize) { createPhotographerListItem(profileImage = createImage()) },
            page = page,
            pageSize = pageSize,
            totalPage = 10,
        )
        every {
            photographerService.findPhotographerList(
                FindPhotographerListQuery(
                    requestUserId = null,
                    regions = emptySet(),
                    photographyTypes = emptySet(),
                    searchKeyword = null,
                    sort = sort,
                    page = page,
                    pageSize = pageSize,
                ),
            )
        } returns expectedResult

        // when and then
        mvc.perform(
            get("/api/v1/photographers/list")
                .param("sort", sort.name)
                .param("page", page.toString())
                .param("pageSize", pageSize.toString()),
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$.items", hasSize<Int>(expectedResult.items.size)))
            .andExpect(jsonPath("$.page").value(expectedResult.page))
            .andExpect(jsonPath("$.pageSize").value(expectedResult.pageSize))
            .andExpect(jsonPath("$.totalPage").value(expectedResult.totalPage))
        verify {
            photographerService.findPhotographerList(
                FindPhotographerListQuery(
                    requestUserId = null,
                    regions = emptySet(),
                    photographyTypes = emptySet(),
                    searchKeyword = null,
                    sort = sort,
                    page = page,
                    pageSize = pageSize,
                ),
            )
        }
    }

    @Test
    fun `잘못된 형식의 지역 필터링 데이터가 주어지고, 사진작가 리스트를 조회하면, validation exception이 발생한다`() {
        // given
        val page = 1
        val pageSize = randomInt(min = 5, max = 15)

        // when and then
        mvc.perform(
            get("/api/v1/photographers/list")
                .param("regions", "경기 수원 원천")
                .param("sort", PhotographerSortType.LATEST.name)
                .param("page", page.toString())
                .param("pageSize", pageSize.toString()),
        ).andExpect(status().isUnprocessableEntity)
    }

    @Test
    fun `저장된 사진작가 리스트를 조회한다`() {
        // given
        val requestUser = createUser()
        val page = 1
        val pageSize = randomInt(min = 5, max = 15)
        val expectedResult = Pagination(
            items = generateRandomList(maxSize = pageSize) { createPhotographerListItem(profileImage = createImage()) },
            page = page,
            pageSize = pageSize,
            totalPage = 10,
        )
        every {
            photographerService.findSavedPhotographerList(
                FindSavedPhotographerListQuery(
                    requestUserId = requestUser.id,
                    page = page,
                    pageSize = pageSize,
                ),
            )
        } returns expectedResult

        // when and then
        mvc.perform(
            get("/api/v1/photographers/saved")
                .param("page", page.toString())
                .param("pageSize", pageSize.toString())
                .withAuthUser(requestUser),
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$.items", hasSize<Int>(expectedResult.items.size)))
            .andExpect(jsonPath("$.page").value(expectedResult.page))
            .andExpect(jsonPath("$.pageSize").value(expectedResult.pageSize))
            .andExpect(jsonPath("$.totalPage").value(expectedResult.totalPage))
        verify {
            photographerService.findSavedPhotographerList(
                FindSavedPhotographerListQuery(
                    requestUserId = requestUser.id,
                    page = page,
                    pageSize = pageSize,
                ),
            )
        }
    }

    @Test
    fun `닉네임이 주어지고, 닉네임의 사용 여부를 조회한다`() {
        // given
        val nickname = randomString()
        val expectedResult = randomBoolean()
        every {
            photographerService.existsNickname(
                ExistsPhotographerNicknameQuery(nickname),
            )
        } returns expectedResult

        // when and then
        mvc.perform(
            get("/api/v1/photographers/nicknames/existence")
                .param("nickname", nickname),
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$.nickname").value(nickname))
            .andExpect(jsonPath("$.exists").value(expectedResult))
        verify {
            photographerService.existsNickname(
                ExistsPhotographerNicknameQuery(nickname),
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
        every { photographerService.register(request.toCommand(userId)) } returns expectedResult

        // when and then
        mvc.perform(
            post("/api/v1/photographers/me/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
                .withAuthUser(createUser(id = userId)),
        ).andExpect(status().isOk)
        verify { photographerService.register(request.toCommand(userId)) }
    }

    @Test
    fun `사진작가를 저장한다`() {
        // given
        val reqUser = createUser(id = randomLong())
        val photographerId = randomLong()
        val command = SavePhotographerCommand(requestUserId = reqUser.id, photographerId = photographerId)
        every { photographerService.savePhotographer(command) } just runs

        // when and then
        mvc.perform(
            post("/api/v1/photographers/$photographerId/save")
                .withAuthUser(reqUser),
        ).andExpect(status().isNoContent)
        verify { photographerService.savePhotographer(command) }
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
            photographerService.updatePhotographerProfile(requestBody.toCommand(photographerId))
        } returns expectedResult

        // when and then
        mvc.perform(
            put("/api/v1/photographers/me/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(requestBody))
                .withAuthUser(createUser(id = photographerId)),
        ).andExpect(status().isOk)
        verify { photographerService.updatePhotographerProfile(requestBody.toCommand(photographerId)) }
    }

    @Test
    fun `내 작가 페이지를 수정한다`() {
        // given
        val photographerId = randomLong()
        val expectedResult = createPhotographer(id = photographerId)
        val requestBody = UpdateMyPhotographerPageRequest(
            portfolio = generateRandomList(maxSize = 3) { createImageRequest() },
            address = null,
            instagramId = null,
            contactLink = null,
            description = randomString(len = randomInt(min = 1, max = 300)),
        )
        every {
            photographerService.updatePhotographerPage(requestBody.toCommand(photographerId))
        } returns expectedResult

        // when and then
        mvc.perform(
            put("/api/v1/photographers/me/page")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(requestBody))
                .withAuthUser(createUser(id = photographerId)),
        ).andExpect(status().isOk)
        verify { photographerService.updatePhotographerPage(requestBody.toCommand(photographerId)) }
    }

    @Test
    fun `사진작가 저장을 해제한다`() {
        // given
        val reqUser = createUser(id = randomLong())
        val photographerId = randomLong()
        val command = UnsavePhotographerCommand(requestUserId = reqUser.id, photographerId = photographerId)
        every { photographerService.unsavePhotographer(command) } just runs

        // when and then
        mvc.perform(
            delete("/api/v1/photographers/$photographerId/unsave")
                .withAuthUser(reqUser),
        ).andExpect(status().isNoContent)
        verify { photographerService.unsavePhotographer(command) }
    }
}
