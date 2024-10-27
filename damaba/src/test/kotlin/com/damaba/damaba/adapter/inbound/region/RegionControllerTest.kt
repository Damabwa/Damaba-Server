package com.damaba.damaba.adapter.inbound.region

import com.damaba.damaba.application.port.inbound.region.FindRegionGroupsUseCase
import com.damaba.damaba.config.ControllerTestConfig
import com.damaba.damaba.util.TestFixture.createRegionGroups
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
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

@ActiveProfiles("test")
@Import(ControllerTestConfig::class)
@WebMvcTest(RegionController::class)
class RegionControllerTest @Autowired constructor(
    private val mvc: MockMvc,
    private val findRegionGroupsUseCase: FindRegionGroupsUseCase,
) {
    @TestConfiguration
    class MockBeanSetUp {
        @Bean
        fun findRegionGroupsUseCase(): FindRegionGroupsUseCase = mockk()
    }

    @Test
    fun `전체 지역 리스트를 조회한다`() {
        // given
        val expectedResult = createRegionGroups()
        every { findRegionGroupsUseCase.findRegionGroups() } returns expectedResult

        // when & then
        mvc.perform(
            get("/api/v1/regions/groups"),
        ).andExpect(status().isOk)
            .andExpect(jsonPath("regionGroups.size()").value(expectedResult.size))
        verify { findRegionGroupsUseCase.findRegionGroups() }
    }
}
