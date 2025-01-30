package com.damaba.damaba.application.port.outbound.promotion

interface CountSavedPromotionPort {
    fun countByPromotionId(promotionId: Long): Long
}
