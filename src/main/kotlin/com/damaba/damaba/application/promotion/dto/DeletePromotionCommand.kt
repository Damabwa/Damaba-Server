package com.damaba.damaba.application.promotion.dto

import com.damaba.damaba.domain.user.User

data class DeletePromotionCommand(
    val requestUser: User,
    val promotionId: Long,
)
