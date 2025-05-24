package com.damaba.damaba.application.port.inbound.photographer

import com.damaba.damaba.domain.common.PageValidator
import com.damaba.damaba.domain.common.Pagination
import com.damaba.damaba.domain.common.constant.PhotographyType
import com.damaba.damaba.domain.photographer.PhotographerListItem
import com.damaba.damaba.domain.photographer.constant.PhotographerSortType
import com.damaba.damaba.domain.region.RegionFilterCondition

interface FindPhotographerListUseCase {
    fun findPhotographerList(query: Query): Pagination<PhotographerListItem>

    data class Query(
        val requestUserId: Long?,
        val regions: Set<RegionFilterCondition>,
        val photographyTypes: Set<PhotographyType>,
        val sort: PhotographerSortType,
        val page: Int,
        val pageSize: Int,
    ) {
        init {
            PageValidator.validate(page, pageSize)
        }
    }
}
