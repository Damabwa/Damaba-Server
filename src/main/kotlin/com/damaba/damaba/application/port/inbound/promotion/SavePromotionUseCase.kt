package com.damaba.damaba.application.port.inbound.promotion

interface SavePromotionUseCase {
    fun savePromotion(command: Command)

    data class Command(
        val userId: Long,
        val promotionId: Long,
    )
}
