package com.damaba.user.application.service.user

import com.damaba.common_exception.ValidationException
import com.damaba.common_file.application.port.outbound.UploadFilePort
import com.damaba.common_file.domain.FileUploadRollbackEvent
import com.damaba.common_file.domain.UploadedFile
import com.damaba.user.application.port.inbound.user.CheckNicknameExistenceUseCase
import com.damaba.user.application.port.inbound.user.UpdateMyInfoUseCase
import com.damaba.user.application.port.outbound.common.PublishEventPort
import com.damaba.user.application.port.outbound.user.CheckNicknameExistencePort
import com.damaba.user.application.port.outbound.user.GetUserPort
import com.damaba.user.application.port.outbound.user.UpdateUserPort
import com.damaba.user.domain.user.User
import com.damaba.user.domain.user.constant.Gender
import com.damaba.user.domain.user.exception.NicknameAlreadyExistsException
import com.damaba.user.util.RandomTestUtils.Companion.randomBoolean
import com.damaba.user.util.RandomTestUtils.Companion.randomLocalDate
import com.damaba.user.util.RandomTestUtils.Companion.randomLong
import com.damaba.user.util.RandomTestUtils.Companion.randomString
import com.damaba.user.util.TestFixture.createUploadFile
import com.damaba.user.util.TestFixture.createUser
import io.mockk.Runs
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.Test

class UserServiceTest {
    private val getUserPort: GetUserPort = mockk()
    private val checkNicknameExistencePort: CheckNicknameExistencePort = mockk()
    private val updateUserPort: UpdateUserPort = mockk()
    private val uploadFilePort: UploadFilePort = mockk()
    private val publishEventPort: PublishEventPort = mockk()
    private val sut = UserService(
        getUserPort,
        checkNicknameExistencePort,
        updateUserPort,
        uploadFilePort,
        publishEventPort,
    )

    @Test
    fun `내 user id가 주어지고, 내 정보를 조회한다`() {
        // given
        val userId = randomLong()
        val expectedResult = createUser()
        every { getUserPort.getById(userId) } returns expectedResult

        // when
        val actualResult = sut.getMyInfo(userId)

        // then
        verify { getUserPort.getById(userId) }
        confirmVerifiedEveryMocks()
        assertThat(actualResult).isEqualTo(expectedResult)
    }

    @Test
    fun `닉네임이 주어지고, 주어진 닉네임이 존재하는지 확인한다`() {
        // given
        val nickname = randomString(len = 7)
        val command = CheckNicknameExistenceUseCase.Command(nickname)
        val expectedResult = randomBoolean()
        every { checkNicknameExistencePort.doesNicknameExist(nickname) } returns expectedResult

        // when
        val actualResult = sut.doesNicknameExist(command)

        // then
        verify { checkNicknameExistencePort.doesNicknameExist(nickname) }
        confirmVerifiedEveryMocks()
        assertThat(actualResult).isEqualTo(expectedResult)
    }

    @ValueSource(strings = ["", "   ", "1", "over7chars", "with!@#$%^"])
    @ParameterizedTest
    fun `유효하지 않은 닉네임이 주어지고, 유저 정보를 수정하면, validation 예외가 발생한다 `(nickname: String) {
        // given

        // when
        val ex = catchThrowable {
            sut.updateMyInfo(
                UpdateMyInfoUseCase.Command(
                    userId = 1L,
                    nickname = nickname,
                    gender = null,
                    birthDate = null,
                    instagramId = null,
                    profileImage = null,
                ),
            )
        }

        // then
        assertThat(ex).isInstanceOf(ValidationException::class.java)
    }

    @ValueSource(strings = ["", "   ", "over30chars,over30chars,over30chars"])
    @ParameterizedTest
    fun `유효하지 않은 인스타그램 id가 주어지고, 유저 정보를 수정하면, validation 예외가 발생한다 `(instagramId: String) {
        // given

        // when
        val ex = catchThrowable {
            sut.updateMyInfo(
                UpdateMyInfoUseCase.Command(
                    userId = 1L,
                    nickname = null,
                    gender = null,
                    birthDate = null,
                    instagramId = instagramId,
                    profileImage = null,
                ),
            )
        }

        // then
        assertThat(ex).isInstanceOf(ValidationException::class.java)
    }

    @Test
    fun `수정할 유저 정보가 주어지고, 유저 정보를 수정하면, 수정된 유저 정보가 반환된다`() {
        // given
        val userId = randomLong()
        val user = createUser(id = userId)
        val newNickname = randomString(len = 7)
        val newGender = Gender.FEMALE
        val newBirthDate = randomLocalDate()
        val newInstagramId = randomString()
        val newProfileImage = createUploadFile()
        val uploadedFile = UploadedFile(randomString(), randomString())
        val command =
            UpdateMyInfoUseCase.Command(userId, newNickname, newGender, newBirthDate, newInstagramId, newProfileImage)
        val expectedResult = createUser(
            nickname = newNickname,
            gender = newGender,
            birthDate = newBirthDate,
            instagramId = newInstagramId,
            profileImageUrl = uploadedFile.url,
        )

        every { checkNicknameExistencePort.doesNicknameExist(newNickname) } returns false
        every { getUserPort.getById(userId) } returns user
        every { uploadFilePort.upload(newProfileImage, any(String::class)) } returns uploadedFile
        every { updateUserPort.update(any(User::class)) } returns expectedResult

        // when
        val actualResult = sut.updateMyInfo(command)

        // then
        verifyOrder {
            checkNicknameExistencePort.doesNicknameExist(newNickname)
            getUserPort.getById(userId)
            uploadFilePort.upload(newProfileImage, any(String::class))
            updateUserPort.update(any(User::class))
        }
        confirmVerifiedEveryMocks()
        assertThat(actualResult.id).isEqualTo(expectedResult.id)
        assertThat(actualResult.nickname).isEqualTo(expectedResult.nickname)
        assertThat(actualResult.gender).isEqualTo(expectedResult.gender)
        assertThat(actualResult.birthDate).isEqualTo(expectedResult.birthDate)
        assertThat(actualResult.instagramId).isEqualTo(expectedResult.instagramId)
        assertThat(actualResult.profileImageUrl).isEqualTo(expectedResult.profileImageUrl)
    }

    @Test
    fun `수정할 유저 닉네임이 주어지고, 유저 정보를 수정한다, 만약 수정할 닉네임이 이미 사용중일 경우 예외가 발생한다`() {
        // given
        val userId = randomLong()
        val existingNickname = randomString(len = 7)
        val command = UpdateMyInfoUseCase.Command(userId, existingNickname, null, null, null, null)
        every { checkNicknameExistencePort.doesNicknameExist(existingNickname) } returns true

        // when
        val ex = catchThrowable { sut.updateMyInfo(command) }

        // then
        verify { checkNicknameExistencePort.doesNicknameExist(existingNickname) }
        confirmVerifiedEveryMocks()
        assertThat(ex).isInstanceOf(NicknameAlreadyExistsException::class.java)
    }

    @Test
    fun `수정할 유저의 생년월일이 주어지고, 유저 정보를 수정하면, 수정된 유저 정보가 반환된다`() {
        // given
        val userId = randomLong()
        val user = createUser(id = userId)
        val newBirthDate = randomLocalDate()
        val command = UpdateMyInfoUseCase.Command(userId, null, null, newBirthDate, null, null)
        val expectedResult = createUser(
            nickname = user.nickname,
            gender = user.gender,
            birthDate = newBirthDate,
            instagramId = user.instagramId ?: "",
            profileImageUrl = user.profileImageUrl,
        )

        every { getUserPort.getById(userId) } returns user
        every { updateUserPort.update(any(User::class)) } returns expectedResult

        // when
        val actualResult = sut.updateMyInfo(command)

        // then
        verifyOrder {
            getUserPort.getById(userId)
            updateUserPort.update(any(User::class))
        }
        confirmVerifiedEveryMocks()
        assertThat(actualResult.id).isEqualTo(expectedResult.id)
        assertThat(actualResult.birthDate).isEqualTo(expectedResult.birthDate)
    }

    @Test
    fun `수정할 유저 정보가 주어지고, 유저 정보를 수정한다, 만약 유저 수정에 실패했다면 예외가 발생한다`() {
        // given
        val userId = randomLong()
        val originalUser = createUser(id = userId)
        val newNickname = randomString(len = 7)
        val command = UpdateMyInfoUseCase.Command(userId, newNickname, null, null, null, null)
        val expectedThrownException = IllegalStateException()

        every { checkNicknameExistencePort.doesNicknameExist(newNickname) } returns false
        every { getUserPort.getById(userId) } returns originalUser
        every { updateUserPort.update(any(User::class)) } throws expectedThrownException // 알 수 없는 에러 발생

        // when
        val ex = catchThrowable { sut.updateMyInfo(command) }

        // then
        verifyOrder {
            checkNicknameExistencePort.doesNicknameExist(newNickname)
            getUserPort.getById(userId)
            updateUserPort.update(any(User::class))
        }
        confirmVerifiedEveryMocks()
        assertThat(ex).isInstanceOf(expectedThrownException::class.java)
    }

    @Test
    fun `수정할 유저 정보가 주어지고, 유저 정보를 수정한다, 만약 파일 업로드에는 성공했으나 유저 수정에는 실패했다면, 파일 업로드를 취소하는 이벤트를 발행한다`() {
        // given
        val userId = randomLong()
        val user = createUser(id = userId)
        val newNickname = randomString(len = 7)
        val newGender = Gender.FEMALE
        val newBirthDate = randomLocalDate()
        val newInstagramId = randomString()
        val newProfileImage = createUploadFile()
        val uploadedFile = UploadedFile(randomString(), randomString())
        val command =
            UpdateMyInfoUseCase.Command(userId, newNickname, newGender, newBirthDate, newInstagramId, newProfileImage)
        val expectedThrownException = IllegalStateException()

        every { checkNicknameExistencePort.doesNicknameExist(newNickname) } returns false
        every { getUserPort.getById(userId) } returns user
        every { uploadFilePort.upload(newProfileImage, any(String::class)) } returns uploadedFile
        every { updateUserPort.update(any(User::class)) } throws expectedThrownException // 알 수 없는 에러 발생
        every { publishEventPort.publish(FileUploadRollbackEvent(listOf(uploadedFile))) } just Runs

        // when
        val ex = catchThrowable { sut.updateMyInfo(command) }

        // then
        verifyOrder {
            checkNicknameExistencePort.doesNicknameExist((newNickname))
            getUserPort.getById(userId)
            uploadFilePort.upload(newProfileImage, any(String::class))
            updateUserPort.update(any(User::class))
            publishEventPort.publish(FileUploadRollbackEvent(listOf(uploadedFile)))
        }
        confirmVerifiedEveryMocks()
        assertThat(ex).isInstanceOf(expectedThrownException::class.java)
    }

    private fun confirmVerifiedEveryMocks() {
        confirmVerified(getUserPort, checkNicknameExistencePort, updateUserPort, uploadFilePort, publishEventPort)
    }
}
