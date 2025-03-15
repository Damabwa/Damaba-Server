package com.damaba.damaba.application.port.inbound.photographer

import com.damaba.damaba.domain.exception.ValidationException
import com.damaba.damaba.domain.photographer.constant.PhotographerSortType
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import kotlin.test.Test

class FindPhotographerListUseCaseQueryTest {
    @Test
    fun `page 번호가 음수라면 validation exception이 발생한다`() {
        // given

        // when
        val ex = catchThrowable {
            FindPhotographerListUseCase.Query(
                reqUserId = null,
                regions = emptySet(),
                photographyTypes = emptySet(),
                sort = PhotographerSortType.LATEST,
                page = -1,
                pageSize = 10,
            )
        }

        // then
        assertThat(ex).isNotNull()
        assertThat(ex).isInstanceOf(ValidationException::class.java)
    }

    @Test
    fun `pageSize가 음수라면 validation exception이 발생한다`() {
        // given

        // when
        val ex = catchThrowable {
            FindPhotographerListUseCase.Query(
                reqUserId = null,
                regions = emptySet(),
                photographyTypes = emptySet(),
                sort = PhotographerSortType.LATEST,
                page = 1,
                pageSize = -1,
            )
        }

        // then
        assertThat(ex).isNotNull()
        assertThat(ex).isInstanceOf(ValidationException::class.java)
    }
}
