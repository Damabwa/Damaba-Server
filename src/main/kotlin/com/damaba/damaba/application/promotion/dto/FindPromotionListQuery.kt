package com.damaba.damaba.application.promotion.dto

import com.damaba.damaba.domain.common.PageValidator
import com.damaba.damaba.domain.common.constant.PhotographyType
import com.damaba.damaba.domain.promotion.constant.PromotionProgressStatus
import com.damaba.damaba.domain.promotion.constant.PromotionSortType
import com.damaba.damaba.domain.promotion.constant.PromotionType
import com.damaba.damaba.domain.region.RegionFilterCondition

data class FindPromotionListQuery(
    val requestUserId: Long?,
    val type: PromotionType?,
    val progressStatus: PromotionProgressStatus?,
    val regions: Set<RegionFilterCondition>,
    val photographyTypes: Set<PhotographyType>,
    val sortType: PromotionSortType,
    val page: Int,
    val pageSize: Int,
) {
    init {
        PageValidator.validate(page, pageSize)
    }
}
