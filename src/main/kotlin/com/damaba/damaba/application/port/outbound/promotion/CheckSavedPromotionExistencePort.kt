package com.damaba.damaba.application.port.outbound.promotion

interface CheckSavedPromotionExistencePort {
    fun existsByUserIdAndPostId(userId: Long, promotionId: Long): Boolean
}
