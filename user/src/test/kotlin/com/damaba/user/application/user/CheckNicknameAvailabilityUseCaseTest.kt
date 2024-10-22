package com.damaba.user.application.user

import com.damaba.user.domain.user.UserService
import com.damaba.user.util.RandomTestUtils.Companion.randomString
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class CheckNicknameAvailabilityUseCaseTest {
    private val userService: UserService = mockk()
    private val sut: CheckNicknameAvailabilityUseCase = CheckNicknameAvailabilityUseCase(userService)

    @ValueSource(booleans = [true, false])
    @ParameterizedTest
    fun `닉네임이 주어지고, 주어진 닉네임의 이용가능성을 확인한다`(doesNicknameExist: Boolean) {
        // given
        val nickname = randomString(len = 5)
        every { userService.doesNicknameExist(nickname) } returns doesNicknameExist

        // when
        val result = sut.invoke(CheckNicknameAvailabilityUseCase.Command(nickname))

        // then
        verify { userService.doesNicknameExist(nickname) }
        assertThat(result).isEqualTo(!doesNicknameExist)
    }
}
