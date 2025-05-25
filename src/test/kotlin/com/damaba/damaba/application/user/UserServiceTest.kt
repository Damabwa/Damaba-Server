package com.damaba.damaba.application.user

import com.damaba.damaba.application.user.dto.ExistsUserNicknameQuery
import com.damaba.damaba.application.user.dto.RegisterUserCommand
import com.damaba.damaba.application.user.dto.UpdateUserProfileCommand
import com.damaba.damaba.domain.file.Image
import com.damaba.damaba.domain.user.constant.Gender
import com.damaba.damaba.domain.user.constant.UserType
import com.damaba.damaba.domain.user.exception.NicknameAlreadyExistsException
import com.damaba.damaba.domain.user.exception.UserAlreadyRegisteredException
import com.damaba.damaba.infrastructure.user.UserRepository
import com.damaba.damaba.util.RandomTestUtils.Companion.randomBoolean
import com.damaba.damaba.util.RandomTestUtils.Companion.randomLong
import com.damaba.damaba.util.RandomTestUtils.Companion.randomString
import com.damaba.damaba.util.RandomTestUtils.Companion.randomUrl
import com.damaba.damaba.util.fixture.FileFixture.createImage
import com.damaba.damaba.util.fixture.UserFixture.createUser
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import io.mockk.verifyOrder
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import kotlin.test.Test

class UserServiceTest {
    private val userRepo: UserRepository = mockk()
    private val sut = UserService(userRepo)

    private fun confirmVerifiedEveryMocks() {
        confirmVerified(userRepo)
    }

    @Test
    fun `내 user id가 주어지고, 내 정보를 조회한다`() {
        // given
        val userId = randomLong()
        val expectedResult = createUser()
        every { userRepo.getById(userId) } returns expectedResult

        // when
        val actualResult = sut.getUser(userId)

        // then
        verify { userRepo.getById(userId) }
        confirmVerifiedEveryMocks()
        assertThat(actualResult).isEqualTo(expectedResult)
    }

    @Test
    fun `닉네임이 주어지고, 주어진 닉네임이 존재하는지 확인한다`() {
        // given
        val nickname = randomString(len = 7)
        val query = ExistsUserNicknameQuery(nickname)
        val expectedResult = randomBoolean()
        every { userRepo.existsNickname(nickname) } returns expectedResult

        // when
        val actualResult = sut.existsNickname(query)

        // then
        verify { userRepo.existsNickname(nickname) }
        confirmVerifiedEveryMocks()
        assertThat(actualResult).isEqualTo(expectedResult)
    }

    @Test
    fun `유저 등록 정보가 주어지고, 유저를 등록하면, 등록된 유저가 반환된다`() {
        // given
        val userId = randomLong()
        val originalUser = createUser(id = userId, type = UserType.UNDEFINED)
        val command = RegisterUserCommand(
            userId = userId,
            nickname = randomString(len = 7),
            gender = Gender.FEMALE,
            instagramId = randomString(len = 15),
        )
        val expectedResult = createUser(
            nickname = command.nickname,
            gender = command.gender,
            instagramId = command.instagramId,
        )
        every { userRepo.getById(userId) } returns originalUser
        every { userRepo.existsNickname(command.nickname) } returns false
        every { userRepo.update(originalUser) } returns expectedResult

        // when
        val actualResult = sut.register(command)

        // then
        verifyOrder {
            userRepo.getById(userId)
            userRepo.existsNickname(command.nickname)
            userRepo.update(originalUser)
        }
        confirmVerifiedEveryMocks()
        assertThat(actualResult.id).isEqualTo(expectedResult.id)
        assertThat(actualResult.nickname).isEqualTo(expectedResult.nickname)
        assertThat(actualResult.gender).isEqualTo(expectedResult.gender)
        assertThat(actualResult.instagramId).isEqualTo(expectedResult.instagramId)
    }

    @Test
    fun `이미 등록된 유저가 다시 등록하면, 예외가 발생한다`() {
        // given
        val userId = randomLong()
        val user = createUser(id = userId, type = UserType.USER)
        val command = RegisterUserCommand(
            userId = userId,
            nickname = randomString(len = 7),
            gender = Gender.FEMALE,
            instagramId = randomString(len = 15),
        )
        every { userRepo.getById(userId) } returns user

        // when
        val ex = catchThrowable { sut.register(command) }

        // then
        verify { userRepo.getById(userId) }
        confirmVerifiedEveryMocks()
        assertThat(ex).isInstanceOf(UserAlreadyRegisteredException::class.java)
    }

    @Test
    fun `다른 유저가 사용중인 닉네임이 유저 등록 정보로 주어지고, 유저를 등록하면, 예외가 발생한다`() {
        // given
        val userId = randomLong()
        val originalUser = createUser(id = userId, type = UserType.UNDEFINED)
        val command = RegisterUserCommand(
            userId = userId,
            nickname = randomString(len = 7),
            gender = Gender.FEMALE,
            instagramId = randomString(len = 15),
        )
        every { userRepo.getById(userId) } returns originalUser
        every { userRepo.existsNickname(command.nickname) } returns true

        // when
        val ex = catchThrowable { sut.register(command) }

        // then
        verifyOrder {
            userRepo.getById(userId)
            userRepo.existsNickname(command.nickname)
        }
        confirmVerifiedEveryMocks()
        assertThat(ex).isInstanceOf(NicknameAlreadyExistsException::class.java)
    }

    @Test
    fun `수정할 유저 정보가 주어지고, 유저 정보를 수정하면, 수정된 유저 정보가 반환된다`() {
        // given
        val userId = randomLong()
        val originalUser = createUser(id = userId, profileImage = null)
        val newNickname = randomString(len = 7)
        val newGender = Gender.FEMALE
        val newInstagramId = null
        val newProfileImageUrl = Image(randomString(), randomUrl())
        val command = UpdateUserProfileCommand(userId, newNickname, newInstagramId, newProfileImageUrl)
        val expectedResult = createUser(
            nickname = newNickname,
            gender = newGender,
            instagramId = newInstagramId,
            profileImage = newProfileImageUrl,
        )
        every { userRepo.getById(userId) } returns originalUser
        every { userRepo.existsNickname(newNickname) } returns false
        every { userRepo.update(originalUser) } returns expectedResult

        // when
        val actualResult = sut.updateUserProfile(command)

        // then
        verifyOrder {
            userRepo.getById(userId)
            userRepo.existsNickname(newNickname)
            userRepo.update(originalUser)
        }
        confirmVerifiedEveryMocks()
        assertThat(actualResult).isEqualTo(expectedResult)
        assertThat(actualResult.id).isEqualTo(expectedResult.id)
        assertThat(actualResult.nickname).isEqualTo(expectedResult.nickname)
        assertThat(actualResult.gender).isEqualTo(expectedResult.gender)
        assertThat(actualResult.instagramId).isEqualTo(expectedResult.instagramId)
        assertThat(actualResult.profileImage).isEqualTo(expectedResult.profileImage)
    }

    @Test
    fun `변경할 닉네임이 주어지고, 유저 정보를 수정하면, 수정된 유저 정보가 반환된다`() {
        // given
        val userId = randomLong()
        val user = createUser(id = userId)
        val newNickname = randomString(len = 7)
        val command = UpdateUserProfileCommand(userId, newNickname, user.instagramId, user.profileImage!!)
        val expectedResult = createUser(
            nickname = newNickname,
            gender = user.gender,
            instagramId = user.instagramId,
            profileImage = user.profileImage!!,
        )
        every { userRepo.getById(userId) } returns user
        every { userRepo.existsNickname(newNickname) } returns false
        every { userRepo.update(user) } returns expectedResult

        // when
        val actualResult = sut.updateUserProfile(command)

        // then
        verifyOrder {
            userRepo.getById(userId)
            userRepo.existsNickname(newNickname)
            userRepo.update(user)
        }
        confirmVerifiedEveryMocks()
        assertThat(actualResult.id).isEqualTo(expectedResult.id)
        assertThat(actualResult.nickname).isEqualTo(expectedResult.nickname)
        assertThat(actualResult.gender).isEqualTo(expectedResult.gender)
        assertThat(actualResult.instagramId).isEqualTo(expectedResult.instagramId)
        assertThat(actualResult.profileImage).isEqualTo(expectedResult.profileImage)
    }

    @Test
    fun `변경할 프로필 이미지가 주어지고, 유저 정보를 수정하면, 수정된 유저 정보가 반환된다`() {
        // given
        val userId = randomLong()
        val originalProfileImage = createImage()
        val user = createUser(id = userId, profileImage = originalProfileImage)
        val newProfileImageUrl = createImage()
        val command = UpdateUserProfileCommand(userId, user.nickname, user.instagramId, newProfileImageUrl)
        val expectedResult = createUser(
            nickname = user.nickname,
            gender = user.gender,
            instagramId = user.instagramId,
            profileImage = newProfileImageUrl,
        )
        every { userRepo.getById(userId) } returns user
        every { userRepo.deleteByUrl(originalProfileImage.url) } just runs
        every { userRepo.update(user) } returns expectedResult

        // when
        val actualResult = sut.updateUserProfile(command)

        // then
        verifyOrder {
            userRepo.getById(userId)
            userRepo.deleteByUrl(originalProfileImage.url)
            userRepo.update(user)
        }
        confirmVerifiedEveryMocks()
        assertThat(actualResult.id).isEqualTo(expectedResult.id)
        assertThat(actualResult.nickname).isEqualTo(expectedResult.nickname)
        assertThat(actualResult.gender).isEqualTo(expectedResult.gender)
        assertThat(actualResult.instagramId).isEqualTo(expectedResult.instagramId)
        assertThat(actualResult.profileImage).isEqualTo(expectedResult.profileImage)
    }

    @Test
    fun `수정할 유저 닉네임이 주어지고, 유저 정보를 수정한다, 만약 수정할 닉네임이 다른 유저가 이미 사용중일 경우 예외가 발생한다`() {
        // given
        val userId = randomLong()
        val existingNickname = randomString(len = 7)
        val command = UpdateUserProfileCommand(
            userId,
            existingNickname,
            randomString(),
            Image(randomString(), randomUrl()),
        )
        every { userRepo.getById(userId) } returns createUser(id = userId)
        every { userRepo.existsNickname(existingNickname) } returns true

        // when
        val ex = catchThrowable { sut.updateUserProfile(command) }

        // then
        verifyOrder {
            userRepo.getById(userId)
            userRepo.existsNickname(existingNickname)
        }
        confirmVerifiedEveryMocks()
        assertThat(ex).isInstanceOf(NicknameAlreadyExistsException::class.java)
    }
}
