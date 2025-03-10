package com.damaba.damaba.application.port.outbound.photographer

import com.damaba.damaba.domain.common.Pagination
import com.damaba.damaba.domain.common.PhotographyType
import com.damaba.damaba.domain.photographer.PhotographerListItem
import com.damaba.damaba.domain.photographer.constant.PhotographerSortType
import com.damaba.damaba.domain.region.RegionFilterCondition

interface FindPhotographerListPort {
    fun find(
        reqUserId: Long?,
        regions: Set<RegionFilterCondition>,
        photographyTypes: Set<PhotographyType>,
        sort: PhotographerSortType,
        page: Int,
        pageSize: Int,
    ): Pagination<PhotographerListItem>
}
