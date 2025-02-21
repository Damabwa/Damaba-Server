package com.damaba.damaba.adapter.outbound.photographer

import com.damaba.damaba.config.RepositoryTestConfig
import com.damaba.damaba.domain.photographer.SavedPhotographer
import com.damaba.damaba.util.RandomTestUtils.Companion.randomLong
import org.assertj.core.api.Assertions.assertThat
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import kotlin.test.Test

@ActiveProfiles("test")
@Import(RepositoryTestConfig::class, SavedPhotographerCoreRepository::class)
@DataJpaTest
class SavedPhotographerCoreRepositoryTest @Autowired constructor(
    private val sut: SavedPhotographerCoreRepository,
    private val savedPhotographerJpaRepository: SavedPhotographerJpaRepository,
) {
    @Test
    fun `프로모션 저장 이력 존재 여부를 조회한다, 만약 존재한다면 true가 반환된다`() {
        // given
        val userId = randomLong()
        val photographerId = randomLong()
        sut.create(SavedPhotographer.create(userId, photographerId))

        // when
        val result = sut.existsByUserIdAndPhotographerId(userId, photographerId)

        // then
        assertThat(result).isTrue()
    }

    @Test
    fun `프로모션 저장 이력 존재 여부를 조회한다, 만약 존재하지 않는다면 false가 반환된다`() {
        // when
        val result = sut.existsByUserIdAndPhotographerId(randomLong(), randomLong())

        // then
        assertThat(result).isFalse()
    }

    @Test
    fun `사진작가 저장 이력을 생성 및 저장한다`() {
        // given
        val userId = randomLong()
        val photographerId = randomLong()

        // when
        sut.create(SavedPhotographer.create(userId, photographerId))

        // then
        val result = savedPhotographerJpaRepository.findAll()
        assertThat(result.size).isEqualTo(1)
    }
}
