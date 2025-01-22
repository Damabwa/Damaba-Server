package com.damaba.damaba.adapter.outbound.promotion

import com.damaba.damaba.application.port.outbound.promotion.CheckSavedPromotionExistencePort
import com.damaba.damaba.application.port.outbound.promotion.CreateSavedPromotionPort
import com.damaba.damaba.domain.promotion.SavedPromotion
import org.springframework.stereotype.Repository

@Repository
class SavedPromotionCoreRepository(
    private val savedPromotionJpaRepository: SavedPromotionJpaRepository,
) : CreateSavedPromotionPort,
    CheckSavedPromotionExistencePort {
    override fun create(savedPromotion: SavedPromotion) {
        savedPromotionJpaRepository.save(
            SavedPromotionJpaEntity(
                savedPromotion.userId,
                savedPromotion.promotionId,
            ),
        )
    }

    override fun existsByUserIdAndPostId(
        userId: Long,
        promotionId: Long,
    ): Boolean = savedPromotionJpaRepository.existsByUserIdAndPromotionId(userId, promotionId)
}
