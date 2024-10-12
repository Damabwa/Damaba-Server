package com.damaba.user.domain.user

import com.damaba.user.domain.user.constant.LoginType
import com.damaba.user.util.RandomTestUtils.Companion.randomLong
import com.damaba.user.util.RandomTestUtils.Companion.randomString
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import kotlin.test.Test

class UserServiceTest {
    private val userRepository = mockk<UserRepository>()
    private val sut = UserService(userRepository)

    @Test
    fun `유저의 OAuth login user id가 주어지고, 주어진 uid에 해당하는 유저를 조회한다`() {
        // given
        val oAuthUserId = randomString()
        val expectedResult = createUser()
        every { userRepository.findByOAuthLoginUid(oAuthUserId) } returns expectedResult

        // when
        val actualResult = sut.findUserByOAuthLoginUid(oAuthUserId)

        // then
        verify { userRepository.findByOAuthLoginUid(oAuthUserId) }
        assertThat(actualResult).isEqualTo(expectedResult)
    }

    @Test
    fun `신규 유저를 생성 및 저장한다`() {
        // given
        val oAuthLoginUid = randomString()
        val loginType = LoginType.KAKAO
        val expectedResult = createUser(oAuthLoginUid = oAuthLoginUid, loginType = loginType)
        every { userRepository.save(any(User::class)) } returns expectedResult

        // when
        val actualResult = sut.createNewUser(oAuthLoginUid, loginType)

        // then
        verify { userRepository.save(any(User::class)) }
        assertThat(actualResult).isEqualTo(expectedResult)
    }

    private fun createUser(
        id: Long = randomLong(),
        oAuthLoginUid: String = randomString(),
        loginType: LoginType = LoginType.KAKAO,
    ): User = User(
        id = id,
        oAuthLoginUid = oAuthLoginUid,
        loginType = loginType,
    )
}
