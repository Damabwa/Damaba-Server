package com.damaba.damaba.application.service.region

import com.damaba.damaba.infrastructure.region.RegionRepository
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
    private val regionRepo: RegionRepository = mockk()
    private val sut: RegionService = RegionService(regionRepo)

    private fun confirmVerifiedEveryMocks() {
        confirmVerified(regionRepo)
    }

    @Test
    fun `전체 지역 카테고리 리스트를 조회한다`() {
        // given
        val expectedResult = generateRandomList(maxSize = 10) { randomString() }
        every { regionRepo.findRegionCategories() } returns expectedResult

        // when
        val actualResult = sut.findRegionCategories()

        // then
        verify { regionRepo.findRegionCategories() }
        confirmVerifiedEveryMocks()
        assertIterableEquals(expectedResult, actualResult)
    }

    @Test
    fun `전체 지역 리스트를 조회한다`() {
        // given
        val expectedResult = createRegionGroups()
        every { regionRepo.findRegionGroups() } returns expectedResult

        // when
        val actualResult = sut.findRegionGroups()

        // then
        verify { regionRepo.findRegionGroups() }
        confirmVerifiedEveryMocks()
        assertIterableEquals(expectedResult, actualResult)
    }
}
