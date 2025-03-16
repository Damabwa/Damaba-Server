package com.damaba.damaba.adapter.outbound.promotion

import com.damaba.damaba.application.port.outbound.promotion.CountPromotionSavePort
import com.damaba.damaba.application.port.outbound.promotion.CreatePromotionSavePort
import com.damaba.damaba.application.port.outbound.promotion.DeletePromotionSavePort
import com.damaba.damaba.application.port.outbound.promotion.ExistsPromotionSavePort
import com.damaba.damaba.application.port.outbound.promotion.GetPromotionSavePort
import com.damaba.damaba.domain.promotion.PromotionSave
import com.damaba.damaba.domain.promotion.exception.PromotionSaveNotFoundException
import org.springframework.stereotype.Repository

@Repository
class PromotionSaveCoreRepository(
    private val promotionSaveJpaRepository: PromotionSaveJpaRepository,
) : GetPromotionSavePort,
    ExistsPromotionSavePort,
    CountPromotionSavePort,
    CreatePromotionSavePort,
    DeletePromotionSavePort {
    override fun getByUserIdAndPromotionId(userId: Long, promotionId: Long): PromotionSave {
        val promotionSaveJpaEntity = promotionSaveJpaRepository
            .findByUserIdAndPromotionId(userId, promotionId)
            ?: throw PromotionSaveNotFoundException()
        return promotionSaveJpaEntity.toPromotionSave()
    }

    override fun existsByUserIdAndPromotionId(
        userId: Long,
        promotionId: Long,
    ): Boolean = promotionSaveJpaRepository.existsByUserIdAndPromotionId(userId, promotionId)

    override fun countByPromotionId(promotionId: Long): Long = promotionSaveJpaRepository.countByPromotionId(promotionId)

    override fun create(promotionSave: PromotionSave) {
        promotionSaveJpaRepository.save(
            PromotionSaveJpaEntity(
                promotionSave.userId,
                promotionSave.promotionId,
            ),
        )
    }

    override fun delete(promotionSave: PromotionSave) {
        promotionSaveJpaRepository.deleteById(promotionSave.id)
    }
}
