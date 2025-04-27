package com.damaba.damaba.application.port.outbound.promotion

interface CountPromotionSavePort {
    fun countByPromotionId(promotionId: Long): Long
}
