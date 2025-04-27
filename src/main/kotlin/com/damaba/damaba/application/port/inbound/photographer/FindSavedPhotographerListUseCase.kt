package com.damaba.damaba.application.port.inbound.photographer

import com.damaba.damaba.domain.common.PageValidator
import com.damaba.damaba.domain.common.Pagination
import com.damaba.damaba.domain.photographer.PhotographerListItem

interface FindSavedPhotographerListUseCase {
    fun findSavedPhotographerList(query: Query): Pagination<PhotographerListItem>

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
