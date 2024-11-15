package com.damaba.damaba.application.service.region

import com.damaba.damaba.application.port.outbound.region.FindRegionCategoriesPort
import com.damaba.damaba.application.port.outbound.region.FindRegionGroupsPort
import com.damaba.damaba.util.RandomTestUtils.Companion.generateRandomList
import com.damaba.damaba.util.RandomTestUtils.Companion.randomString
import com.damaba.damaba.util.fixture.RegionFixture.createRegionGroups
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertIterableEquals
import kotlin.test.Test

class RegionServiceTest {
    private val findRegionCategoriesPort: FindRegionCategoriesPort = mockk()
    private val findRegionGroupsPort: FindRegionGroupsPort = mockk()
    private val sut: RegionService = RegionService(
        findRegionCategoriesPort,
        findRegionGroupsPort,
    )

    private fun confirmVerifiedEveryMocks() {
        confirmVerified(findRegionCategoriesPort, findRegionGroupsPort)
    }

    @Test
    fun `전체 지역 카테고리 리스트를 조회한다`() {
        // given
        val expectedResult = generateRandomList(maxSize = 10) { randomString() }
        every { findRegionCategoriesPort.findRegionCategories() } returns expectedResult

        // when
        val actualResult = sut.findRegionCategories()

        // then
        verify { findRegionCategoriesPort.findRegionCategories() }
        confirmVerifiedEveryMocks()
        assertIterableEquals(expectedResult, actualResult)
    }

    @Test
    fun `전체 지역 리스트를 조회한다`() {
        // given
        val expectedResult = createRegionGroups()
        every { findRegionGroupsPort.findRegionGroups() } returns expectedResult

        // when
        val actualResult = sut.findRegionGroups()

        // then
        verify { findRegionGroupsPort.findRegionGroups() }
        confirmVerifiedEveryMocks()
        assertIterableEquals(expectedResult, actualResult)
    }
}
