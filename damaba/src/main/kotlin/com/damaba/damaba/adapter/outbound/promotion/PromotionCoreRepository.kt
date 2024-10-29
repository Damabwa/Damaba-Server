package com.damaba.damaba.adapter.outbound.promotion

import com.damaba.damaba.adapter.outbound.common.toPagination
import com.damaba.damaba.application.port.outbound.promotion.FindPromotionsPort
import com.damaba.damaba.application.port.outbound.promotion.GetPromotionPort
import com.damaba.damaba.application.port.outbound.promotion.SavePromotionPort
import com.damaba.damaba.domain.common.Pagination
import com.damaba.damaba.domain.promotion.Promotion
import com.damaba.damaba.domain.promotion.exception.PromotionNotFoundException
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Repository

@Repository
class PromotionCoreRepository(
    private val promotionJpaRepository: PromotionJpaRepository,
) : GetPromotionPort,
    FindPromotionsPort,
    SavePromotionPort {
    override fun getById(id: Long): Promotion =
        getJpaEntityById(id).toDomain()

    override fun findPromotions(page: Int, pageSize: Int): Pagination<Promotion> =
        promotionJpaRepository.findAll(PageRequest.of(page, pageSize))
            .toPagination { promotionJpaEntity -> promotionJpaEntity.toDomain() }

    override fun save(promotion: Promotion): Promotion {
        val promotionJpaEntity = promotionJpaRepository.save(PromotionJpaEntity.from(promotion))
        return promotionJpaEntity.toDomain()
    }

    private fun getJpaEntityById(id: Long): PromotionJpaEntity =
        promotionJpaRepository.findById(id).orElseThrow { PromotionNotFoundException() }
}
