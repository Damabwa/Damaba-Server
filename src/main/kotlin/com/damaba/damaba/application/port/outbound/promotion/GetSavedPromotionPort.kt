package com.damaba.damaba.application.port.outbound.promotion

import com.damaba.damaba.domain.promotion.SavedPromotion

interface GetSavedPromotionPort {
    fun getByUserIdAndPromotionId(userId: Long, promotionId: Long): SavedPromotion
}
