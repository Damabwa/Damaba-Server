package com.damaba.damaba.application.port.inbound.promotion

import com.damaba.damaba.domain.common.PageValidator
import com.damaba.damaba.domain.common.Pagination
import com.damaba.damaba.domain.common.PhotographyType
import com.damaba.damaba.domain.promotion.PromotionListItem
import com.damaba.damaba.domain.promotion.constant.PromotionProgressStatus
import com.damaba.damaba.domain.promotion.constant.PromotionSortType
import com.damaba.damaba.domain.promotion.constant.PromotionType
import com.damaba.damaba.domain.region.RegionFilterCondition

interface FindPromotionListUseCase {
    fun findPromotionList(query: Query): Pagination<PromotionListItem>

    data class Query(
        val reqUserId: Long?,
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
}
