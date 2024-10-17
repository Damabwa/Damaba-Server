package com.damaba.user.application.user

import com.damaba.user.domain.user.UserService
import com.damaba.user.domain.user.constant.Gender
import com.damaba.user.util.RandomTestUtils.Companion.randomInt
import com.damaba.user.util.RandomTestUtils.Companion.randomLong
import com.damaba.user.util.RandomTestUtils.Companion.randomString
import com.damaba.user.util.TestFixture.createUploadFile
import com.damaba.user.util.TestFixture.createUser
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import kotlin.test.Test

class UpdateMyInfoUseCaseTest {
    private val userService: UserService = mockk()
    private val sut: UpdateMyInfoUseCase = UpdateMyInfoUseCase(userService)

    @Test
    fun `수정할 유저 정보가 주어지고, 유저 정보를 수정하면, 수정된 유저 정보가 반환된다`() {
        // given
        val userId = randomLong()
        val newNickname = randomString()
        val newGender = Gender.FEMALE
        val newAge = randomInt()
        val newInstagramId = randomString()
        val newProfileImage = createUploadFile()
        val expectedResult = createUser(id = userId)
        every {
            userService.updateUserInfo(
                userId,
                newNickname,
                newGender,
                newAge,
                newInstagramId,
                newProfileImage,
            )
        } returns expectedResult

        // when
        val actualResult =
            sut.invoke(UpdateMyInfoUseCase.Command(userId, newNickname, newGender, newAge, newInstagramId, newProfileImage))

        // then
        verify { userService.updateUserInfo(userId, newNickname, newGender, newAge, newInstagramId, newProfileImage) }
        assertThat(actualResult).isEqualTo(expectedResult)
    }
}
