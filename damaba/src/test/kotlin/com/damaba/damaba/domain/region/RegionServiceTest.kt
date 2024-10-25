package com.damaba.damaba.domain.region

import com.damaba.damaba.util.TestFixture.createRegionGroups
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertIterableEquals
import kotlin.test.Test

class RegionServiceTest {
    private val regionRepository: RegionRepository = mockk()
    private val sut: RegionService = RegionService(regionRepository)

    @Test
    fun `전체 지역 리스트를 조회한다`() {
        // given
        val expectedResult = createRegionGroups()
        every { regionRepository.findRegionGroups() } returns expectedResult

        // when
        val actualResult = sut.findRegionGroups()

        // then
        assertIterableEquals(expectedResult, actualResult)
    }
}
