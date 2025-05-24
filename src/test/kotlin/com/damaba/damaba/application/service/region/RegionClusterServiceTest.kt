package com.damaba.damaba.application.service.region

import com.damaba.damaba.infrastructure.region.RegionClusterRepository
import com.damaba.damaba.util.fixture.RegionFixture.createRegionClusters
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertIterableEquals
import kotlin.test.Test

class RegionClusterServiceTest {
    private val regionClusterRepo: RegionClusterRepository = mockk()
    private val sut = RegionClusterService(regionClusterRepo)

    private fun confirmVerifiedEveryMocks() {
        confirmVerified(regionClusterRepo)
    }

    @Test
    fun `Region cluter 리스트를 조회한다`() {
        // given
        val expectedResult = createRegionClusters()
        every { regionClusterRepo.findRegionClusters() } returns expectedResult

        // when
        val actualResult = sut.findRegionClusters()

        // then
        verify { regionClusterRepo.findRegionClusters() }
        confirmVerifiedEveryMocks()
        assertIterableEquals(expectedResult, actualResult)
    }
}
