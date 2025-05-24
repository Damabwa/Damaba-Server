package com.damaba.damaba.infrastructure.promotion

import com.damaba.damaba.config.RepositoryTestConfig
import com.damaba.damaba.domain.promotion.PromotionSave
import com.damaba.damaba.domain.promotion.exception.PromotionSaveNotFoundException
import com.damaba.damaba.util.RandomTestUtils.Companion.randomLong
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import kotlin.test.Test

@ActiveProfiles("test")
@Import(RepositoryTestConfig::class, PromotionSaveCoreRepository::class)
@DataJpaTest
class PromotionSaveCoreRepositoryTest @Autowired constructor(
    private val sut: PromotionSaveCoreRepository,
    private val promotionSaveJpaRepository: PromotionSaveJpaRepository,
) {
    @Test
    fun `(GET) 유저 id와 프로모션 id가 주어지고, 프로모션 저장 이력을 조회한다`() {
        // given
        val userId = randomLong()
        val promotionId = randomLong()
        sut.create(PromotionSave.create(userId, promotionId))

        // when
        val result = sut.getByUserIdAndPromotionId(userId, promotionId)

        // then
        assertThat(result.id).isGreaterThan(0L)
        assertThat(result.userId).isEqualTo(userId)
        assertThat(result.promotionId).isEqualTo(promotionId)
    }

    @Test
    fun `(GET) 유저 id와 프로모션 id가 주어지고, 프로모션 저장 이력을 조회한다, 일치하는 저장 이력이 없다면 예외가 발생한다`() {
        // when
        val ex = catchThrowable { sut.getByUserIdAndPromotionId(randomLong(), randomLong()) }

        // then
        assertThat(ex).isInstanceOf(PromotionSaveNotFoundException::class.java)
    }

    @Test
    fun `프로모션 저장 이력 존재 여부를 조회한다, 만약 존재한다면 true가 반환된다`() {
        // given
        val userId = randomLong()
        val promotionId = randomLong()
        sut.create(PromotionSave.create(userId, promotionId))

        // when
        val result = sut.existsByUserIdAndPromotionId(userId, promotionId)

        // then
        assertThat(result).isTrue()
    }

    @Test
    fun `프로모션 저장 이력 존재 여부를 조회한다, 만약 존재하지 않는다면 false가 반환된다`() {
        // when
        val result = sut.existsByUserIdAndPromotionId(randomLong(), randomLong())

        // then
        assertThat(result).isFalse()
    }

    @Test
    fun `신규 프로모션 저장 이력을 저장한다`() {
        // given
        val userId = randomLong()
        val promotionId = randomLong()

        // when
        sut.create(PromotionSave.create(userId, promotionId))

        // then
        val result = promotionSaveJpaRepository.findAll()
        assertThat(result.size).isEqualTo(1)
    }

    @Test
    fun `프로모션 저장 이력을 삭제한다`() {
        // given
        val userId = randomLong()
        val promotionId = randomLong()
        sut.create(PromotionSave.create(userId, promotionId))
        val promotionSave = sut.getByUserIdAndPromotionId(userId, promotionId)

        // when
        sut.delete(promotionSave)

        // then
        val result = promotionSaveJpaRepository.findAll()
        assertThat(result.size).isEqualTo(0)
    }
}
