package com.damaba.damaba.application.promotion.dto

data class UnsavePromotionCommand(
    val userId: Long,
    val promotionId: Long,
)
