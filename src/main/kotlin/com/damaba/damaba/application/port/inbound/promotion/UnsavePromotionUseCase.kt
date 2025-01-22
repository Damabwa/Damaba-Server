package com.damaba.damaba.application.port.inbound.promotion

interface UnsavePromotionUseCase {
    fun unsavePromotion(command: Command)

    data class Command(
        val userId: Long,
        val promotionId: Long,
    )
}
