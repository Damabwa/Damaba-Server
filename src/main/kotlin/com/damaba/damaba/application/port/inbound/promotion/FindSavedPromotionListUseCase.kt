package com.damaba.damaba.application.port.inbound.promotion

import com.damaba.damaba.domain.common.PageValidator
import com.damaba.damaba.domain.common.Pagination
import com.damaba.damaba.domain.promotion.PromotionListItem

interface FindSavedPromotionListUseCase {
    fun findSavedPromotionList(query: Query): Pagination<PromotionListItem>

    data class Query(
        val requestUserId: Long,
        val page: Int,
        val pageSize: Int,
    ) {
        init {
            PageValidator.validate(page, pageSize)
        }
    }
}
