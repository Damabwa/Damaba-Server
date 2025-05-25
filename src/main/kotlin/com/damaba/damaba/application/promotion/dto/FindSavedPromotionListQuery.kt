package com.damaba.damaba.application.promotion.dto

import com.damaba.damaba.domain.common.PageValidator

data class FindSavedPromotionListQuery(
    val requestUserId: Long,
    val page: Int,
    val pageSize: Int,
) {
    init {
        PageValidator.validate(page, pageSize)
    }
}
