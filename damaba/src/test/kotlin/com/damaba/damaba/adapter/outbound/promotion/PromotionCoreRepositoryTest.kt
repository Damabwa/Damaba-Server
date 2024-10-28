package com.damaba.damaba.adapter.outbound.promotion

import com.damaba.damaba.domain.promotion.exception.PromotionNotFoundException
import com.damaba.damaba.util.TestFixture.createPromotion
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIterable
import org.assertj.core.api.Assertions.catchThrowable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import kotlin.test.Test

@ActiveProfiles("test")
@Import(PromotionCoreRepository::class)
@DataJpaTest
class PromotionCoreRepositoryTest @Autowired constructor(
    private val promotionCoreRepository: PromotionCoreRepository,
) {
    @Test
    fun `신규 프로모션을 저장한다`() {
        // given
        val promotion = createPromotion()

        // when
        val savedPromotion = promotionCoreRepository.save(promotion)

        // then
        assertThat(savedPromotion.id).isGreaterThan(0)
        assertThat(savedPromotion.type).isEqualTo(promotion.type)
        assertThat(savedPromotion.title).isEqualTo(promotion.title)
        assertThat(savedPromotion.content).isEqualTo(promotion.content)
        assertThatIterable(savedPromotion.images).isEqualTo(promotion.images)
        assertThatIterable(savedPromotion.activeRegions).isEqualTo(promotion.activeRegions)
        assertThatIterable(savedPromotion.hashtags).isEqualTo(promotion.hashtags)
    }

    @Test
    fun `(Get) 프로모션 id가 주어지고, 주어진 id와 일치하는 프로모션을 단건 조회하면, 조회된 프로모션이 반환된다`() {
        // given
        val promotion = promotionCoreRepository.save(createPromotion())

        // when
        val result = promotionCoreRepository.getById(promotion.id)

        // then
        assertThat(result).isEqualTo(promotion)
    }

    @Test
    fun `(Get) 프로모션 id가 주어지고, 주어진 id와 일치하는 프로모션을 단건 조회한다, 만약 일치하는 프로모션이 없다면 예외가 발생한다`() {
        // given

        // when
        val ex = catchThrowable { promotionCoreRepository.getById(1L) }

        // then
        assertThat(ex).isInstanceOf(PromotionNotFoundException::class.java)
    }
}
