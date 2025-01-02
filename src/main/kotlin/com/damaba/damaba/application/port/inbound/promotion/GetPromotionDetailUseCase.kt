package com.damaba.damaba.application.port.inbound.promotion

import com.damaba.damaba.domain.promotion.Promotion

interface GetPromotionDetailUseCase {
    fun getPromotionDetail(promotionId: Long): Promotion
}
