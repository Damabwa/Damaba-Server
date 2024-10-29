package com.damaba.damaba.application.port.inbound.promotion

import com.damaba.common_exception.ValidationException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import kotlin.test.Test

class FindPromotionsUseCaseQueryTest {
    @Test
    fun `page 번호가 음수라면 validation exception이 발생한다`() {
        // given

        // when
        val ex = catchThrowable {
            FindPromotionsUseCase.Query(page = -1, pageSize = 10)
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
            FindPromotionsUseCase.Query(page = 10, pageSize = -1)
        }

        // then
        assertThat(ex).isNotNull()
        assertThat(ex).isInstanceOf(ValidationException::class.java)
    }
}
