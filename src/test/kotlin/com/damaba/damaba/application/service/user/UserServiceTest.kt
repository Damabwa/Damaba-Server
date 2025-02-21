package com.damaba.damaba.application.service.user

import com.damaba.damaba.application.port.inbound.user.ExistsUserNicknameUseCase
import com.damaba.damaba.application.port.inbound.user.RegisterUserUseCase
import com.damaba.damaba.application.port.inbound.user.UpdateUserUseCase
import com.damaba.damaba.application.port.outbound.common.PublishEventPort
import com.damaba.damaba.application.port.outbound.user.ExistsNicknamePort
import com.damaba.damaba.application.port.outbound.user.GetUserPort
import com.damaba.damaba.application.port.outbound.user.UpdateUserPort
import com.damaba.damaba.domain.file.DeleteFileEvent
import com.damaba.damaba.domain.file.Image
import com.damaba.damaba.domain.user.constant.Gender
import com.damaba.damaba.domain.user.constant.UserType
import com.damaba.damaba.domain.user.exception.NicknameAlreadyExistsException
import com.damaba.damaba.domain.user.exception.UserAlreadyRegisteredException
import com.damaba.damaba.util.RandomTestUtils.Companion.randomBoolean
import com.damaba.damaba.util.RandomTestUtils.Companion.randomLong
import com.damaba.damaba.util.RandomTestUtils.Companion.randomString
import com.damaba.damaba.util.RandomTestUtils.Companion.randomUrl
import com.damaba.damaba.util.fixture.UserFixture.createUser
import io.mockk.Runs
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import kotlin.test.Test

class UserServiceTest {
    private val getUserPort: GetUserPort = mockk()
    private val existsNicknamePort: ExistsNicknamePort = mockk()
    private val updateUserPort: UpdateUserPort = mockk()
    private val publishEventPort: PublishEventPort = mockk()
    private val sut = UserService(
        getUserPort,
        existsNicknamePort,
        updateUserPort,
        publishEventPort,
    )

    private fun confirmVerifiedEveryMocks() {
        confirmVerified(getUserPort, existsNicknamePort, updateUserPort, publishEventPort)
    }

    @Test
    fun `내 user id가 주어지고, 내 정보를 조회한다`() {
        // given
        val userId = randomLong()
        val expectedResult = createUser()
        every { getUserPort.getById(userId) } returns expectedResult

        // when
        val actualResult = sut.getUser(userId)

        // then
        verify { getUserPort.getById(userId) }
        confirmVerifiedEveryMocks()
        assertThat(actualResult).isEqualTo(expectedResult)
    }

    @Test
    fun `닉네임이 주어지고, 주어진 닉네임이 존재하는지 확인한다`() {
        // given
        val nickname = randomString(len = 7)
        val query = ExistsUserNicknameUseCase.Query(nickname)
        val expectedResult = randomBoolean()
        every { existsNicknamePort.existsNickname(nickname) } returns expectedResult

        // when
        val actualResult = sut.existsNickname(query)

        // then
        verify { existsNicknamePort.existsNickname(nickname) }
        confirmVerifiedEveryMocks()
        assertThat(actualResult).isEqualTo(expectedResult)
    }

    @Test
    fun `유저 등록 정보가 주어지고, 유저를 등록하면, 등록된 유저가 반환된다`() {
        // given
        val userId = randomLong()
        val originalUser = createUser(id = userId, type = UserType.UNDEFINED)
        val command = RegisterUserUseCase.Command(
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
        every { getUserPort.getById(userId) } returns originalUser
        every { existsNicknamePort.existsNickname(command.nickname) } returns false
        every { updateUserPort.update(originalUser) } returns expectedResult

        // when
        val actualResult = sut.register(command)

        // then
        verifyOrder {
            getUserPort.getById(userId)
            existsNicknamePort.existsNickname(command.nickname)
            updateUserPort.update(originalUser)
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
        val command = RegisterUserUseCase.Command(
            userId = userId,
            nickname = randomString(len = 7),
            gender = Gender.FEMALE,
            instagramId = randomString(len = 15),
        )
        every { getUserPort.getById(userId) } returns user

        // when
        val ex = catchThrowable { sut.register(command) }

        // then
        verify { getUserPort.getById(userId) }
        confirmVerifiedEveryMocks()
        assertThat(ex).isInstanceOf(UserAlreadyRegisteredException::class.java)
    }

    @Test
    fun `다른 유저가 사용중인 닉네임이 유저 등록 정보로 주어지고, 유저를 등록하면, 예외가 발생한다`() {
        // given
        val userId = randomLong()
        val originalUser = createUser(id = userId, type = UserType.UNDEFINED)
        val command = RegisterUserUseCase.Command(
            userId = userId,
            nickname = randomString(len = 7),
            gender = Gender.FEMALE,
            instagramId = randomString(len = 15),
        )
        every { getUserPort.getById(userId) } returns originalUser
        every { existsNicknamePort.existsNickname(command.nickname) } returns true

        // when
        val ex = catchThrowable { sut.register(command) }

        // then
        verifyOrder {
            getUserPort.getById(userId)
            existsNicknamePort.existsNickname(command.nickname)
        }
        confirmVerifiedEveryMocks()
        assertThat(ex).isInstanceOf(NicknameAlreadyExistsException::class.java)
    }

    @Test
    fun `수정할 유저 정보가 주어지고, 유저 정보를 수정하면, 수정된 유저 정보가 반환된다`() {
        // given
        val userId = randomLong()
        val user = createUser(id = userId)
        val newNickname = randomString(len = 7)
        val newGender = Gender.FEMALE
        val newInstagramId = null
        val newProfileImageUrl = Image(randomString(), randomUrl())
        val command = UpdateUserUseCase.Command(userId, newNickname, newInstagramId, newProfileImageUrl)
        val expectedResult = createUser(
            nickname = newNickname,
            gender = newGender,
            instagramId = newInstagramId,
            profileImage = newProfileImageUrl,
        )
        every { getUserPort.getById(userId) } returns user
        every { existsNicknamePort.existsNickname(newNickname) } returns false
        every { publishEventPort.publish(any(DeleteFileEvent::class)) } just Runs
        every { updateUserPort.update(user) } returns expectedResult

        // when
        val actualResult = sut.updateUser(command)

        // then
        verifyOrder {
            getUserPort.getById(userId)
            existsNicknamePort.existsNickname(newNickname)
            publishEventPort.publish(any(DeleteFileEvent::class))
            updateUserPort.update(user)
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
        val command = UpdateUserUseCase.Command(userId, newNickname, user.instagramId, user.profileImage)
        val expectedResult = createUser(
            nickname = newNickname,
            gender = user.gender,
            instagramId = user.instagramId,
            profileImage = user.profileImage,
        )
        every { getUserPort.getById(userId) } returns user
        every { existsNicknamePort.existsNickname(newNickname) } returns false
        every { updateUserPort.update(user) } returns expectedResult

        // when
        val actualResult = sut.updateUser(command)

        // then
        verifyOrder {
            getUserPort.getById(userId)
            existsNicknamePort.existsNickname(newNickname)
            updateUserPort.update(user)
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
        val user = createUser(id = userId)
        val newProfileImageUrl = Image(randomString(), randomUrl())
        val command = UpdateUserUseCase.Command(userId, user.nickname, user.instagramId, newProfileImageUrl)
        val expectedResult = createUser(
            nickname = user.nickname,
            gender = user.gender,
            instagramId = user.instagramId,
            profileImage = newProfileImageUrl,
        )
        every { getUserPort.getById(userId) } returns user
        every { publishEventPort.publish(any(DeleteFileEvent::class)) } just Runs
        every { updateUserPort.update(user) } returns expectedResult

        // when
        val actualResult = sut.updateUser(command)

        // then
        verifyOrder {
            getUserPort.getById(userId)
            publishEventPort.publish(any(DeleteFileEvent::class))
            updateUserPort.update(user)
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
        val command = UpdateUserUseCase.Command(
            userId,
            existingNickname,
            randomString(),
            Image(randomString(), randomUrl()),
        )
        every { getUserPort.getById(userId) } returns createUser(id = userId)
        every { existsNicknamePort.existsNickname(existingNickname) } returns true

        // when
        val ex = catchThrowable { sut.updateUser(command) }

        // then
        verifyOrder {
            getUserPort.getById(userId)
            existsNicknamePort.existsNickname(existingNickname)
        }
        confirmVerifiedEveryMocks()
        assertThat(ex).isInstanceOf(NicknameAlreadyExistsException::class.java)
    }
}
