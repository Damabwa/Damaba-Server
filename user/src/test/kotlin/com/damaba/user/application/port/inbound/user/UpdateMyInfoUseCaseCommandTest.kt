package com.damaba.user.application.port.inbound.user

import com.damaba.common_exception.ValidationException
import com.damaba.user.domain.user.UserProfileImage
import com.damaba.user.util.RandomTestUtils.Companion.randomString
import com.damaba.user.util.RandomTestUtils.Companion.randomUrl
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import kotlin.test.Test

class UpdateMyInfoUseCaseCommandTest {
    @Test
    fun `유효하지 않은 닉네임이 입력되면, validation exception이 발생한다`() {
        // given
        val invalidNickname = "A!"

        // when
        val ex = catchThrowable {
            UpdateMyInfoUseCase.Command(
                userId = 1,
                nickname = invalidNickname,
                instagramId = randomString(),
                profileImage = UserProfileImage(randomString(), randomUrl()),
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
            UpdateMyInfoUseCase.Command(
                userId = 1,
                nickname = randomString(len = 5),
                instagramId = invalidInstagramId,
                profileImage = UserProfileImage(randomString(), randomUrl()),
            )
        }

        // then
        assertThat(ex).isInstanceOf(ValidationException::class.java)
    }
}
