package com.damaba.damaba.adapter.outbound.promotion

import com.damaba.damaba.application.port.outbound.GetPromotionPort
import com.damaba.damaba.application.port.outbound.promotion.SavePromotionPort
import com.damaba.damaba.domain.promotion.Promotion
import com.damaba.damaba.domain.promotion.exception.PromotionNotFoundException
import org.springframework.stereotype.Repository

@Repository
class PromotionCoreRepository(
    private val promotionJpaRepository: PromotionJpaRepository,
) : SavePromotionPort,
    GetPromotionPort {
    override fun save(promotion: Promotion): Promotion {
        val promotionJpaEntity = promotionJpaRepository.save(PromotionJpaEntity.from(promotion))
        return promotionJpaEntity.toDomain()
    }

    override fun getById(id: Long): Promotion =
        getJpaEntityById(id).toDomain()

    private fun getJpaEntityById(id: Long): PromotionJpaEntity =
        promotionJpaRepository.findById(id).orElseThrow { PromotionNotFoundException() }
}
