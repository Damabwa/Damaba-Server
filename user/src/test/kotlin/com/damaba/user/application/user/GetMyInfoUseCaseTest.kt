package com.damaba.user.application.user

import com.damaba.user.domain.user.UserService
import com.damaba.user.util.RandomTestUtils.Companion.randomLong
import com.damaba.user.util.TestFixture.createUser
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import kotlin.test.Test

class GetMyInfoUseCaseTest {
    private val userService: UserService = mockk()
    private val sut: GetMyInfoUseCase = GetMyInfoUseCase(userService)

    @Test
    fun `내 정보를 조회하면, 내 정보가 담긴 user domain entity가 반환된다`() {
        // given
        val userId = randomLong()
        val expectedResult = createUser(id = userId)
        every { userService.getUserById(userId) } returns expectedResult

        // when
        val actualResult = sut.invoke(userId)

        // then
        verify { userService.getUserById(userId) }
        assertThat(actualResult).isEqualTo(expectedResult)
    }
}
