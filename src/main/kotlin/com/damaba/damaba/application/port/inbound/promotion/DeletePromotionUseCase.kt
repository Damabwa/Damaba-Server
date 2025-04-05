package com.damaba.damaba.application.port.inbound.promotion

import com.damaba.damaba.domain.user.User

interface DeletePromotionUseCase {
    fun deletePromotion(command: Command)

    data class Command(
        val requestUser: User,
        val promotionId: Long,
    )
}
