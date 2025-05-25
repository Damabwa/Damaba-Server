package com.damaba.damaba.controller.region

import com.damaba.damaba.application.region.RegionClusterService
import com.damaba.damaba.config.ControllerTestConfig
import com.damaba.damaba.util.fixture.RegionFixture.createRegionClusters
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import kotlin.test.Test

@Import(ControllerTestConfig::class)
@WebMvcTest(RegionClusterController::class)
class RegionClusterControllerTest @Autowired constructor(
    private val mvc: MockMvc,
    private val regionClusterService: RegionClusterService,
) {
    @TestConfiguration
    class MockBeanSetUp {
        @Bean
        fun regionClusterService(): RegionClusterService = mockk()
    }

    @Test
    fun `Region cluster 리스트를 조회한다`() {
        // given
        val expectedResult = createRegionClusters()
        every { regionClusterService.findRegionClusters() } returns expectedResult

        // when & then
        mvc.perform(
            get("/api/v1/region-clusters"),
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$.regionClusters.size()").value(expectedResult.size))
        verify { regionClusterService.findRegionClusters() }
    }
}
