package com.damaba.user.domain.user

import com.damaba.user.domain.user.constant.Gender
import com.damaba.user.domain.user.constant.LoginType
import com.damaba.user.domain.user.exception.NicknameAlreadyExistsException
import com.damaba.user.util.RandomTestUtils.Companion.randomInt
import com.damaba.user.util.RandomTestUtils.Companion.randomLong
import com.damaba.user.util.RandomTestUtils.Companion.randomString
import com.damaba.user.util.TestFixture.createUser
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import kotlin.test.Test

class UserServiceTest {
    private val userRepository = mockk<UserRepository>()
    private val sut = UserService(userRepository)

    @Test
    fun `유저의 id가 주어지고, 주어진 id에 해당하는 유저를 조회한다`() {
        // given
        val userId = randomLong()
        val expectedResult = createUser()
        every { userRepository.findById(userId) } returns expectedResult

        // when
        val actualResult = sut.findUserById(userId)

        // then
        verify { userRepository.findById(userId) }
        assertThat(actualResult).isEqualTo(expectedResult)
    }

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

    @Test
    fun `수정할 유저 정보가 주어지고, 유저 정보를 수정하면, 수정된 유저 정보가 반환된다`() {
        // given
        val userId = randomLong()
        val user = createUser(id = userId)
        val newNickname = randomString()
        val newGender = Gender.FEMALE
        val newAge = randomInt()
        val newInstagramId = randomString()
        val expectedResult = user.update(newNickname, newGender, newAge, newInstagramId)

        every { userRepository.existsByNickname(newNickname) } returns false
        every { userRepository.getById(userId) } returns user
        every { userRepository.update(expectedResult) } returns expectedResult
        // when
        val actualResult = sut.updateUserInfo(userId, newNickname, newGender, newAge, newInstagramId)

        // then
        verifyOrder {
            userRepository.existsByNickname(newNickname)
            userRepository.getById(userId)
            userRepository.update(expectedResult)
        }
        assertThat(actualResult.id).isEqualTo(expectedResult.id)
        assertThat(actualResult.nickname).isEqualTo(expectedResult.nickname)
        assertThat(actualResult.gender).isEqualTo(expectedResult.gender)
        assertThat(actualResult.age).isEqualTo(expectedResult.age)
        assertThat(actualResult.instagramId).isEqualTo(expectedResult.instagramId)
    }

    @Test
    fun `수정할 유저 닉네임이 주어지고, 유저 정보를 수정한다, 만약 수정할 닉네임이 이미 사용중일 경우 예외가 발생한다`() {
        // given
        val userId = randomLong()
        val existingNickname = randomString()
        every { userRepository.existsByNickname(existingNickname) } returns true

        // when
        val ex = catchThrowable { sut.updateUserInfo(userId, existingNickname, null, null, null) }

        // then
        verify { userRepository.existsByNickname(existingNickname) }
        assertThat(ex).isInstanceOf(NicknameAlreadyExistsException::class.java)
    }

    @Test
    fun `수정할 유저의 나이가 주어지고, 유저 정보를 수정하면, 수정된 유저 정보가 반환된다`() {
        // given
        val userId = randomLong()
        val user = createUser(id = userId)
        val newAge = randomInt()
        val expectedResult = user.update(null, null, newAge, null)

        every { userRepository.getById(userId) } returns user
        every { userRepository.update(expectedResult) } returns expectedResult
        // when
        val actualResult = sut.updateUserInfo(userId, null, null, newAge, null)

        // then
        verifyOrder {
            userRepository.getById(userId)
            userRepository.update(expectedResult)
        }
        assertThat(actualResult.id).isEqualTo(expectedResult.id)
        assertThat(actualResult.age).isEqualTo(expectedResult.age)
    }
}
