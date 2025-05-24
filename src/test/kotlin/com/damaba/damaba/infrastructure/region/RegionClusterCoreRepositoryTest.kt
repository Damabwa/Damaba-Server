package com.damaba.damaba.infrastructure.region

import com.damaba.damaba.config.JpaConfig
import com.linecorp.kotlinjdsl.support.spring.data.jpa.autoconfigure.KotlinJdslAutoConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import kotlin.test.Test

@ActiveProfiles("test")
@AutoConfigureJsonTesters
@Import(
    JpaConfig::class,
    KotlinJdslAutoConfiguration::class,
    RegionClusterCoreRepository::class,
)
@DataJpaTest
class RegionClusterCoreRepositoryTest @Autowired constructor(
    private val regionClusterRepository: RegionClusterCoreRepository,
) {
    @Test
    fun `Region cluster 리스트를 조회한다`() {
        // when
        val regionClusters = regionClusterRepository.findRegionClusters()

        // then
        assertThat(regionClusters).isNotNull()
        assertThat(regionClusters).isNotEmpty()
    }
}
