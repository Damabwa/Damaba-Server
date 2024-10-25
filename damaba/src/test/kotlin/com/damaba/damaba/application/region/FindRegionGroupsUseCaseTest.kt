package com.damaba.damaba.application.region

import com.damaba.damaba.domain.region.RegionService
import com.damaba.damaba.util.TestFixture.createRegionGroups
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertIterableEquals
import kotlin.test.Test

class FindRegionGroupsUseCaseTest {
    private val regionService: RegionService = mockk()
    private val sut: FindRegionGroupsUseCase = FindRegionGroupsUseCase(regionService)

    @Test
    fun `전체 지역 리스트를 조회한다`() {
        // given
        val expectedResult = createRegionGroups()
        every { regionService.findRegionGroups() } returns expectedResult

        // when
        val actualResult = sut.invoke()

        // then
        assertIterableEquals(expectedResult, actualResult)
    }
}
