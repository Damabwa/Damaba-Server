package com.damaba.damaba.adapter.outbound.common

import com.damaba.damaba.domain.common.Pagination
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl

fun <T, R> Page<T>.toPagination(mapper: (T) -> R): Pagination<R> = Pagination(
    items = this.content.map(mapper),
    page = this.number,
    pageSize = this.size,
    totalPage = this.totalPages,
)

fun <T : Any> Page<T?>.filterNotNull(): Page<T> {
    val nonNullContent = this.content.filterNotNull()
    return PageImpl(nonNullContent, this.pageable, nonNullContent.size.toLong())
}
