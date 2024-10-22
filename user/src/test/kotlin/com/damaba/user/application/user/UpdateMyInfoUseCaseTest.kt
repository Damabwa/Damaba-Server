package com.damaba.user.application.user

import com.damaba.common_exception.ValidationException
import com.damaba.user.domain.user.UserService
import com.damaba.user.domain.user.constant.Gender
import com.damaba.user.util.RandomTestUtils.Companion.randomLocalDate
import com.damaba.user.util.RandomTestUtils.Companion.randomLong
import com.damaba.user.util.RandomTestUtils.Companion.randomString
import com.damaba.user.util.TestFixture.createUploadFile
import com.damaba.user.util.TestFixture.createUser
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.Test

class UpdateMyInfoUseCaseTest {
    private val userService: UserService = mockk()
    private val sut: UpdateMyInfoUseCase = UpdateMyInfoUseCase(userService)

    @Test
    fun `수정할 유저 정보가 주어지고, 유저 정보를 수정하면, 수정된 유저 정보가 반환된다`() {
        // given
        val userId = randomLong()
        val newNickname = randomString(len = 7)
        val newGender = Gender.FEMALE
        val newBirthDate = randomLocalDate()
        val newInstagramId = null
        val newProfileImage = createUploadFile()
        val expectedResult = createUser(id = userId)
        every {
            userService.updateUserInfo(userId, newNickname, newGender, newBirthDate, newInstagramId, newProfileImage)
        } returns expectedResult

        // when
        val actualResult =
            sut.invoke(
                UpdateMyInfoUseCase.Command(
                    userId,
                    newNickname,
                    newGender,
                    newBirthDate,
                    newInstagramId,
                    newProfileImage,
                ),
            )

        // then
        verify {
            userService.updateUserInfo(
                userId,
                newNickname,
                newGender,
                newBirthDate,
                newInstagramId,
                newProfileImage,
            )
        }
        assertThat(actualResult).isEqualTo(expectedResult)
    }

    @ValueSource(strings = ["", "   ", "A", "ABCDEFGHIJK", "Hello!"])
    @ParameterizedTest
    fun `유효하지 않은 닉네임이 주어지고, 유저 정보를 수정하면, Validation 예외가 발생한다`(invalidNickname: String) {
        // given
        val userId = randomLong()

        // when
        val ex = catchThrowable {
            sut.invoke(UpdateMyInfoUseCase.Command(userId, invalidNickname, null, null, null, null))
        }

        // then
        assertThat(ex).isInstanceOf(ValidationException::class.java)
    }

    @ValueSource(strings = ["", "   ", "1234567890123456789012345678901234567890"])
    @ParameterizedTest
    fun `30글자가 초과된 인스타 아이디가 주어지고, 유저 정보를 수정하면, Validation 예외가 발생한다`(invalidInstagramId: String) {
        // given
        val userId = randomLong()

        // when
        val ex = catchThrowable {
            sut.invoke(UpdateMyInfoUseCase.Command(userId, null, null, null, invalidInstagramId, null))
        }

        // then
        assertThat(ex).isInstanceOf(ValidationException::class.java)
    }
}
