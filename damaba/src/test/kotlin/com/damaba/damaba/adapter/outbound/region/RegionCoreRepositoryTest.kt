package com.damaba.damaba.adapter.outbound.region

import org.assertj.core.api.Assertions.assertThat
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import kotlin.test.Test

@ActiveProfiles("test")
@AutoConfigureJsonTesters
@Import(RegionCoreRepository::class)
@DataJpaTest
class RegionCoreRepositoryTest @Autowired constructor(
    private val regionRepository: RegionCoreRepository,
) {
    @Test
    fun `전체 지역 카테고리 리스트를 조회한다`() {
        // when
        val categories = regionRepository.findRegionCategories()

        // then
        assertThat(categories).isNotNull()
        assertThat(categories).isNotEmpty()
    }

    @Test
    fun `전체 지역 리스트를 조회한다`() {
        // when
        val regionGroups = regionRepository.findRegionGroups()

        // then
        assertThat(regionGroups).isNotNull()
        assertThat(regionGroups).isNotEmpty()
    }
}
