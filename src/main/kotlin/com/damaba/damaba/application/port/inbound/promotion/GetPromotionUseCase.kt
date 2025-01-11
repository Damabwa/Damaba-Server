package com.damaba.damaba.application.port.inbound.promotion

import com.damaba.damaba.domain.promotion.Promotion

interface GetPromotionUseCase {
    fun getPromotion(promotionId: Long): Promotion
}
