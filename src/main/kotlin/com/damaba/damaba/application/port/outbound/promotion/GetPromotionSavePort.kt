package com.damaba.damaba.application.port.outbound.promotion

import com.damaba.damaba.domain.promotion.PromotionSave

interface GetPromotionSavePort {
    fun getByUserIdAndPromotionId(userId: Long, promotionId: Long): PromotionSave
}
