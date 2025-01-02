package com.damaba.damaba.domain.user

import com.damaba.damaba.domain.exception.ValidationException
import com.damaba.user.domain.user.UserValidator
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class UserValidatorTest {
    @ValueSource(strings = ["", "   ", "1", "over7chars", "with!@#$%^"])
    @ParameterizedTest
    fun `유효하지 않은 닉네임이 주어지고, 닉네임 검증을 수행하면, validation exception이 발생한다`(
        invalidNickname: String,
    ) {
        // when
        val ex = catchThrowable { UserValidator.validateUserNickname(invalidNickname) }

        // then
        assertThat(ex).isInstanceOf(ValidationException::class.java)
    }

    @ValueSource(strings = ["", "   ", "over30chars,over30chars,over30chars"])
    @ParameterizedTest
    fun `유효하지 않은 인스타그램 id가 주어지고, 인스타그램 id를 검증하면, validation 예외가 발생한다`(
        invalidInstagramId: String,
    ) {
        // when
        val ex = catchThrowable { UserValidator.validateInstagramId(invalidInstagramId) }

        // then
        assertThat(ex).isInstanceOf(ValidationException::class.java)
    }
}
