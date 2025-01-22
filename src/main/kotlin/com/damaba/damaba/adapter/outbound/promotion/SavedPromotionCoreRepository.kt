package com.damaba.damaba.adapter.outbound.promotion

import com.damaba.damaba.application.port.outbound.promotion.CheckSavedPromotionExistencePort
import com.damaba.damaba.application.port.outbound.promotion.CreateSavedPromotionPort
import com.damaba.damaba.application.port.outbound.promotion.DeleteSavedPromotionPort
import com.damaba.damaba.application.port.outbound.promotion.GetSavedPromotionPort
import com.damaba.damaba.domain.promotion.SavedPromotion
import com.damaba.damaba.domain.promotion.exception.SavedPromotionNotFoundException
import com.damaba.damaba.mapper.PromotionMapper
import org.springframework.stereotype.Repository

@Repository
class SavedPromotionCoreRepository(
    private val savedPromotionJpaRepository: SavedPromotionJpaRepository,
) : GetSavedPromotionPort,
    CheckSavedPromotionExistencePort,
    CreateSavedPromotionPort,
    DeleteSavedPromotionPort {
    override fun getByUserIdAndPromotionId(userId: Long, promotionId: Long): SavedPromotion {
        val savedPromotion = savedPromotionJpaRepository.findByUserIdAndPromotionId(userId, promotionId)
            ?: throw SavedPromotionNotFoundException()
        return PromotionMapper.INSTANCE.toSavedPromotion(savedPromotion)
    }

    override fun existsByUserIdAndPostId(
        userId: Long,
        promotionId: Long,
    ): Boolean = savedPromotionJpaRepository.existsByUserIdAndPromotionId(userId, promotionId)

    override fun create(savedPromotion: SavedPromotion) {
        savedPromotionJpaRepository.save(
            SavedPromotionJpaEntity(
                savedPromotion.userId,
                savedPromotion.promotionId,
            ),
        )
    }

    override fun delete(savedPromotion: SavedPromotion) {
        savedPromotionJpaRepository.deleteById(savedPromotion.id)
    }
}
