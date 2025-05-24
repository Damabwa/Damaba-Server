package com.damaba.damaba.infrastructure.common

import com.damaba.damaba.domain.common.Pagination
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl

fun <T> Page<T>.toPagination(): Pagination<T> = Pagination(
    items = this.content,
    page = this.number,
    pageSize = this.size,
    totalPage = this.totalPages,
)

fun <T : Any> Page<T?>.filterNotNull(): Page<T> {
    val nonNullContent = this.content.filterNotNull()
    return PageImpl(nonNullContent, this.pageable, this.totalElements)
}
