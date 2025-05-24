package com.damaba.damaba.infrastructure.promotion

import com.damaba.damaba.domain.promotion.PromotionSave

interface PromotionSaveRepository {
    fun create(promotionSave: PromotionSave)

    fun getByUserIdAndPromotionId(userId: Long, promotionId: Long): PromotionSave

    fun countByPromotionId(promotionId: Long): Long

    fun existsByUserIdAndPromotionId(userId: Long, promotionId: Long): Boolean

    fun delete(promotionSave: PromotionSave)
}
