package com.damaba.damaba.application.port.outbound.promotion

interface ExistsSavedPromotionPort {
    fun existsByUserIdAndPromotionId(userId: Long, promotionId: Long): Boolean
}
