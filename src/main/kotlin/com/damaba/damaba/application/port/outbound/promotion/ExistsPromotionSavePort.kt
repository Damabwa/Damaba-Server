package com.damaba.damaba.application.port.outbound.promotion

interface ExistsPromotionSavePort {
    fun existsByUserIdAndPromotionId(userId: Long, promotionId: Long): Boolean
}
