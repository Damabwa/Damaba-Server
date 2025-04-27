package com.damaba.damaba.domain.common

import com.damaba.damaba.util.RandomTestUtils.Companion.randomString
import org.assertj.core.api.Assertions.assertThat
import kotlin.test.Test

class PaginationTest {
    @Test
    fun `page가 0이라면 첫 번째 페이지이다`() {
        // given
        val pagination = Pagination(
            items = randomStrings(),
            page = 0,
            pageSize = 10,
            totalPage = 3,
        )

        // then
        assertThat(pagination.isFirstPage).isTrue()
    }

    @Test
    fun `page가 0이 아니라면 첫 번째 페이지가 아니다`() {
        // given
        val pagination = Pagination(
            items = randomStrings(),
            page = 1,
            pageSize = 10,
            totalPage = 3,
        )

        // then
        assertThat(pagination.isFirstPage).isFalse()
    }

    @Test
    fun `page가 totalPage보다 1만큼 작다면 마지막 페이지이다`() {
        // given
        val pagination = Pagination(
            items = randomStrings(),
            page = 2,
            pageSize = 10,
            totalPage = 3,
        )

        // then
        assertThat(pagination.isLastPage).isTrue()
    }

    @Test
    fun `page가 totalPage보다 2만큼 또는 그 이상으로 작다면 마지막 페이지가 아니다`() {
        // given
        val pagination = Pagination(
            items = randomStrings(),
            page = 1,
            pageSize = 10,
            totalPage = 5,
        )

        // then
        assertThat(pagination.isLastPage).isFalse()
    }

    @Test
    fun `첫 번째 페이지가 아니라면 이전 페이지가 존재한다`() {
        // given
        val pagination = Pagination(
            items = randomStrings(),
            page = 1,
            pageSize = 10,
            totalPage = 5,
        )

        // then
        assertThat(pagination.hasPreviousPage).isTrue()
    }

    @Test
    fun `첫 번째 페이지라면 이전 페이지가 존재하지 않는다`() {
        // given
        val pagination = Pagination(
            items = randomStrings(),
            page = 0,
            pageSize = 10,
            totalPage = 5,
        )

        // then
        assertThat(pagination.hasPreviousPage).isFalse()
    }

    @Test
    fun `마지막 페이지가 아니라면 다음 페이지가 존재한다`() {
        // given
        val pagination = Pagination(
            items = randomStrings(),
            page = 1,
            pageSize = 10,
            totalPage = 5,
        )

        // then
        assertThat(pagination.hasNextPage).isTrue()
    }

    @Test
    fun `마지막 페이지라면 다음 페이지가 존재하지 않는다`() {
        // given
        val pagination = Pagination(
            items = randomStrings(),
            page = 1,
            pageSize = 10,
            totalPage = 2,
        )

        // then
        assertThat(pagination.hasNextPage).isFalse()
    }

    private fun randomStrings() = List(10) { randomString() }
}
