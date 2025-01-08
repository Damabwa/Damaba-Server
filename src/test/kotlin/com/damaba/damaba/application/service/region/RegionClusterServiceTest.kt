package com.damaba.damaba.application.service.region

import com.damaba.damaba.application.port.outbound.region.FindRegionClustersPort
import com.damaba.damaba.util.fixture.RegionFixture.createRegionClusters
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertIterableEquals
import kotlin.test.Test

class RegionClusterServiceTest {
    private val findRegionClustersPort: FindRegionClustersPort = mockk()
    private val sut = RegionClusterService(findRegionClustersPort)

    private fun confirmVerifiedEveryMocks() {
        confirmVerified(findRegionClustersPort)
    }

    @Test
    fun `Region cluter 리스트를 조회한다`() {
        // given
        val expectedResult = createRegionClusters()
        every { findRegionClustersPort.findRegionClusters() } returns expectedResult

        // when
        val actualResult = sut.findRegionClusters()

        // then
        verify { findRegionClustersPort.findRegionClusters() }
        confirmVerifiedEveryMocks()
        assertIterableEquals(expectedResult, actualResult)
    }
}
