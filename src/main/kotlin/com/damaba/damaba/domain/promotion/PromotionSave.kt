package com.damaba.damaba.domain.promotion

data class PromotionSave(
    val id: Long,
    val userId: Long,
    val promotionId: Long,
) {
    companion object {
        fun create(userId: Long, promotionId: Long) = PromotionSave(
            id = 0,
            userId = userId,
            promotionId = promotionId,
        )
    }
}
