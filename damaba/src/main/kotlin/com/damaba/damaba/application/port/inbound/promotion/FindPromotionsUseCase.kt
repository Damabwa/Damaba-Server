package com.damaba.damaba.application.port.inbound.promotion

import com.damaba.common_exception.ValidationException
import com.damaba.damaba.domain.common.Pagination
import com.damaba.damaba.domain.promotion.Promotion

interface FindPromotionsUseCase {
    fun findPromotions(query: Query): Pagination<Promotion>

    data class Query(
        val page: Int,
        val pageSize: Int,
    ) {
        init {
            if (page < 0) throw ValidationException("페이지 번호(page)는 0 이상의 정수여야 합니다.")
            if (pageSize < 0) throw ValidationException("페이지 크기(pageSize)는 0 이상의 정수여야 합니다.")
        }
    }
}
