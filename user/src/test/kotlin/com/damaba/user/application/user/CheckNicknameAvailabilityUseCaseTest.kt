package com.damaba.user.application.user

import com.damaba.user.domain.user.UserService
import com.damaba.user.util.RandomTestUtils.Companion.randomBoolean
import com.damaba.user.util.RandomTestUtils.Companion.randomString
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import kotlin.test.Test

class CheckNicknameAvailabilityUseCaseTest {
    private val userService: UserService = mockk()
    private val sut: CheckNicknameAvailabilityUseCase = CheckNicknameAvailabilityUseCase(userService)

    @Test
    fun `닉네임이 주어지고, 주어진 닉네임의 이용가능성을 확인한다`() {
        // given
        val nickname = randomString()
        val expectedResult = randomBoolean()
        every { userService.doesNicknameExist(nickname) } returns expectedResult

        // when
        val actualResult = sut.invoke(nickname)

        // then
        verify { userService.doesNicknameExist(nickname) }
        assertThat(actualResult).isEqualTo(expectedResult)
    }
}
