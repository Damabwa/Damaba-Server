package com.damaba.damaba.application.promotion.dto

data class GetPromotionDetailQuery(
    val requestUserId: Long?,
    val promotionId: Long,
)
