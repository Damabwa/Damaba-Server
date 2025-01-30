package com.damaba.damaba.application.port.outbound.promotion

interface CheckSavedPromotionExistencePort {
    fun existsByUserIdAndPromotionId(userId: Long, promotionId: Long): Boolean
}
