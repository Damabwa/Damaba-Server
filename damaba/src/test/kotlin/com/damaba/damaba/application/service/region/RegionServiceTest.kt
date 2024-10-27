package com.damaba.damaba.application.service.region

import com.damaba.damaba.application.port.outbound.region.FindRegionGroupsPort
import com.damaba.damaba.util.TestFixture.createRegionGroups
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertIterableEquals
import kotlin.test.Test

class RegionServiceTest {
    private val findRegionGroupsPort: FindRegionGroupsPort = mockk()
    private val sut: RegionService = RegionService(findRegionGroupsPort)

    @Test
    fun `전체 지역 리스트를 조회한다`() {
        // given
        val expectedResult = createRegionGroups()
        every { findRegionGroupsPort.findRegionGroups() } returns expectedResult

        // when
        val actualResult = sut.findRegionGroups()

        // then
        assertIterableEquals(expectedResult, actualResult)
    }
}
