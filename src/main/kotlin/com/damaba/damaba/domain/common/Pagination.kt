package com.damaba.damaba.domain.common

/**
 * @property items
 * @property page 페이지 번호. 0부터 시작한다.
 * 페이지 번호는 0부터 시작하기 때문에, 마지막 페이지의 번호(`page`)는 전체 페이지 수(`totalPage`)보다 1 작다.
 * 예를 들어, `totalPage`가 5라면 마지막 페이지의 번호(`page`)는 4이다.
 * @property pageSize 한 페이지에 포홤되는 아이템의 최대 개수
 * @property totalPage 전체 페이지 개수
 * @property hasPreviousPage 이전 페이지 존재 여부
 * @property hasNextPage 다음 페이지 존재 여부
 * @property isFirstPage 첫번째 페이지인지
 * @property isLastPage 마지막 페이지인지
 */
data class Pagination<T>(
    val items: List<T>,
    val page: Int,
    val pageSize: Int,
    val totalPage: Int,
) {
    val isFirstPage: Boolean
        get() = page == 0

    val isLastPage: Boolean
        get() = page == totalPage - 1

    val hasPreviousPage: Boolean
        get() = !isFirstPage

    val hasNextPage: Boolean
        get() = !isLastPage

    fun <R> map(mapper: (T) -> R): Pagination<R> = Pagination(
        items = items.map(mapper),
        page = page,
        pageSize = pageSize,
        totalPage = totalPage,
    )
}
