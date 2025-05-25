package com.damaba.damaba.controller.region

import com.damaba.damaba.application.region.RegionService
import com.damaba.damaba.config.ControllerTestConfig
import com.damaba.damaba.util.RandomTestUtils.Companion.generateRandomList
import com.damaba.damaba.util.RandomTestUtils.Companion.randomString
import com.damaba.damaba.util.fixture.RegionFixture.createRegionGroups
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.hamcrest.Matchers.hasSize
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import kotlin.test.Test

@Suppress("ktlint:standard:annotation")
@ActiveProfiles("test")
@Import(ControllerTestConfig::class)
@WebMvcTest(RegionController::class)
class RegionControllerTest @Autowired constructor(
    private val mvc: MockMvc,
    private val regionService: RegionService,
) {
    @TestConfiguration
    class MockBeanSetUp {
        @Bean
        fun regionService(): RegionService = mockk()
    }

    @Test
    fun `전체 지역 카테고리 리스트를 조회한다`() {
        // given
        val expectedResult = generateRandomList(maxSize = 10) { randomString() }
        every { regionService.findRegionCategories() } returns expectedResult

        // when and then
        mvc.perform(
            get("/api/v1/regions/categories"),
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$.categories", hasSize<Int>(expectedResult.size)))
        verify { regionService.findRegionCategories() }
    }

    @Test
    fun `전체 지역 리스트를 조회한다`() {
        // given
        val expectedResult = createRegionGroups()
        every { regionService.findRegionGroups() } returns expectedResult

        // when and then
        mvc.perform(
            get("/api/v1/regions/groups"),
        ).andExpect(status().isOk)
            .andExpect(jsonPath("regionGroups.size()").value(expectedResult.size))
        verify { regionService.findRegionGroups() }
    }
}
