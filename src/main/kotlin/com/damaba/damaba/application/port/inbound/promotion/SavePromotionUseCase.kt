package com.damaba.damaba.application.port.inbound.promotion

interface SavePromotionUseCase {
    fun savePromotion(query: Query)

    data class Query(
        val userId: Long,
        val promotionId: Long,
    )
}
