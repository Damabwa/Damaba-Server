package com.damaba.damaba.adapter.outbound.promotion

import com.damaba.damaba.config.RepositoryTestConfig
import com.damaba.damaba.domain.promotion.SavedPromotion
import com.damaba.damaba.util.RandomTestUtils.Companion.randomLong
import org.assertj.core.api.Assertions.assertThat
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import kotlin.test.Test

@ActiveProfiles("test")
@Import(RepositoryTestConfig::class, SavedPromotionCoreRepository::class)
@DataJpaTest
class SavedPromotionCoreRepositoryTest @Autowired constructor(
    private val sut: SavedPromotionCoreRepository,
    private val savedPromotionJpaRepository: SavedPromotionJpaRepository,
) {
    @Test
    fun `신규 프로모션 저장 이력을 저장한다`() {
        // given
        val userId = randomLong()
        val promotionId = randomLong()

        // when
        sut.create(SavedPromotion.create(userId, promotionId))

        // then
        val result = savedPromotionJpaRepository.findAll()
        assertThat(result.size).isEqualTo(1)
    }

    @Test
    fun `프로모션 저장 이력 존재 여부를 조회한다, 만약 존재한다면 true가 반환된다`() {
        // given
        val userId = randomLong()
        val promotionId = randomLong()
        sut.create(SavedPromotion.create(userId, promotionId))

        // when
        val result = sut.existsByUserIdAndPostId(userId, promotionId)

        // then
        assertThat(result).isTrue()
    }

    @Test
    fun `프로모션 저장 이력 존재 여부를 조회한다, 만약 존재하지 않는다면 false가 반환된다`() {
        // when
        val result = sut.existsByUserIdAndPostId(randomLong(), randomLong())

        // then
        assertThat(result).isFalse()
    }
}
