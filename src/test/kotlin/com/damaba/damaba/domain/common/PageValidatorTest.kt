package com.damaba.damaba.domain.common

import com.damaba.damaba.domain.exception.ValidationException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import kotlin.test.Test

class PageValidatorTest {
    @Test
    fun `page 번호가 음수라면 validation exception이 발생한다`() {
        // given

        // when
        val ex = catchThrowable { PageValidator.validate(page = -1, pageSize = 10) }

        // then
        assertThat(ex).isNotNull()
        assertThat(ex).isInstanceOf(ValidationException::class.java)
    }

    @Test
    fun `pageSize가 음수라면 validation exception이 발생한다`() {
        // given

        // when
        val ex = catchThrowable { PageValidator.validate(page = 1, pageSize = -1) }

        // then
        assertThat(ex).isNotNull()
        assertThat(ex).isInstanceOf(ValidationException::class.java)
    }
}
