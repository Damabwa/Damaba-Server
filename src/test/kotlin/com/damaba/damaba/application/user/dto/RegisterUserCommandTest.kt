package com.damaba.damaba.application.user.dto

import com.damaba.damaba.domain.exception.ValidationException
import com.damaba.damaba.domain.user.constant.Gender
import com.damaba.damaba.util.RandomTestUtils.Companion.randomString
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import kotlin.test.Test

class RegisterUserCommandTest {
    @Test
    fun `유효하지 않은 닉네임이 입력되면, validation exception이 발생한다`() {
        // given
        val invalidNickname = "A!"

        // when
        val ex = catchThrowable {
            RegisterUserCommand(
                userId = 1,
                nickname = invalidNickname,
                gender = Gender.MALE,
                instagramId = null,
            )
        }

        // then
        assertThat(ex).isInstanceOf(ValidationException::class.java)
    }

    @Test
    fun `유효하지 않은 인스타그램 id가 입력되면, validation exception이 발생한다`() {
        // given
        val invalidInstagramId = "over30chars_123456789012345678901234567890"

        // when
        val ex = catchThrowable {
            RegisterUserCommand(
                userId = 1,
                nickname = randomString(len = 5),
                gender = Gender.FEMALE,
                instagramId = invalidInstagramId,
            )
        }

        // then
        assertThat(ex).isInstanceOf(ValidationException::class.java)
    }
}
