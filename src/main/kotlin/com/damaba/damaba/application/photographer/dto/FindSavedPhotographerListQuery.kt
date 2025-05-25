package com.damaba.damaba.application.photographer.dto

import com.damaba.damaba.domain.common.PageValidator

data class FindSavedPhotographerListQuery(
    val requestUserId: Long,
    val page: Int,
    val pageSize: Int,
) {
    init {
        PageValidator.validate(page, pageSize)
    }
}
