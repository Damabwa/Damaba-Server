package com.damaba.damaba.application.promotion

import com.damaba.damaba.domain.common.PageValidator
import com.damaba.damaba.domain.common.constant.PhotographyType
import com.damaba.damaba.domain.promotion.constant.PromotionProgressStatus
import com.damaba.damaba.domain.promotion.constant.PromotionSortType
import com.damaba.damaba.domain.promotion.constant.PromotionType
import com.damaba.damaba.domain.region.RegionFilterCondition

data class GetPromotionDetailQuery(
    val requestUserId: Long?,
    val promotionId: Long,
)

data class FindSavedPromotionListQuery(
    val requestUserId: Long,
    val page: Int,
    val pageSize: Int,
) {
    init {
        PageValidator.validate(page, pageSize)
    }
}

data class FindPromotionListQuery(
    val requestUserId: Long?,
    val type: PromotionType?,
    val progressStatus: PromotionProgressStatus?,
    val regions: Set<RegionFilterCondition>,
    val photographyTypes: Set<PhotographyType>,
    val searchKeyword: String?,
    val sortType: PromotionSortType,
    val page: Int,
    val pageSize: Int,
) {
    init {
        PageValidator.validate(page, pageSize)
    }
}
