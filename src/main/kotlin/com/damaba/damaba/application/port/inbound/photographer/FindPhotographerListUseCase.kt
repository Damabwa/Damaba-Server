package com.damaba.damaba.application.port.inbound.photographer

import com.damaba.damaba.domain.common.Pagination
import com.damaba.damaba.domain.common.PhotographyType
import com.damaba.damaba.domain.exception.ValidationException
import com.damaba.damaba.domain.photographer.PhotographerListItem
import com.damaba.damaba.domain.photographer.constant.PhotographerSortType
import com.damaba.damaba.domain.region.RegionFilterCondition

interface FindPhotographerListUseCase {
    fun findPhotographerList(query: Query): Pagination<PhotographerListItem>

    data class Query(
        val reqUserId: Long?,
        val regions: Set<RegionFilterCondition>,
        val photographyTypes: Set<PhotographyType>,
        val sort: PhotographerSortType,
        val page: Int,
        val pageSize: Int,
    ) {
        init {
            if (page < 0) throw ValidationException("페이지 번호(page)는 0 이상의 정수여야 합니다.")
            if (pageSize < 0) throw ValidationException("페이지 크기(pageSize)는 0 이상의 정수여야 합니다.")
        }
    }
}
