package com.damaba.damaba.application.photographer.dto

import com.damaba.damaba.domain.common.PageValidator
import com.damaba.damaba.domain.common.constant.PhotographyType
import com.damaba.damaba.domain.photographer.constant.PhotographerSortType
import com.damaba.damaba.domain.region.RegionFilterCondition

data class FindPhotographerListQuery(
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
