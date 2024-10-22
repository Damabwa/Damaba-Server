package com.damaba.user.domain.user

import com.damaba.user.domain.file.FileStorageRepository
import com.damaba.user.domain.file.UploadedFile
import com.damaba.user.domain.user.constant.Gender
import com.damaba.user.domain.user.constant.LoginType
import com.damaba.user.domain.user.exception.NicknameAlreadyExistsException
import com.damaba.user.util.RandomTestUtils.Companion.randomBoolean
import com.damaba.user.util.RandomTestUtils.Companion.randomLocalDate
import com.damaba.user.util.RandomTestUtils.Companion.randomLong
import com.damaba.user.util.RandomTestUtils.Companion.randomString
import com.damaba.user.util.TestFixture.createUploadFile
import com.damaba.user.util.TestFixture.createUser
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import kotlin.test.Test

class UserServiceTest {
    private val userRepository: UserRepository = mockk()
    private val fileStorageRepository: FileStorageRepository = mockk()
    private val sut = UserService(userRepository, fileStorageRepository)

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
        confirmVerifiedEveryMocks()
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
        confirmVerifiedEveryMocks()
        assertThat(actualResult).isEqualTo(expectedResult)
    }

    @Test
    fun `(Get) 유저의 id가 주어지고, 주어진 id에 해당하는 유저를 조회한다`() {
        // given
        val userId = randomLong()
        val expectedResult = createUser()
        every { userRepository.getById(userId) } returns expectedResult

        // when
        val actualResult = sut.getUserById(userId)

        // then
        verify { userRepository.getById(userId) }
        confirmVerifiedEveryMocks()
        assertThat(actualResult).isEqualTo(expectedResult)
    }

    @Test
    fun `닉네임이 주어지고, 주어진 닉네임이 존재하는지 확인한다`() {
        // given
        val nickname = randomString()
        val expectedResult = randomBoolean()
        every { userRepository.existsByNickname(nickname) } returns expectedResult

        // when
        val actualResult = sut.doesNicknameExist(nickname)

        // then
        verify { userRepository.existsByNickname(nickname) }
        confirmVerifiedEveryMocks()
        assertThat(actualResult).isEqualTo(expectedResult)
    }

    @Test
    fun `신규 유저를 생성 및 저장한다`() {
        // given
        val oAuthLoginUid = randomString()
        val loginType = LoginType.KAKAO
        val expectedResult = createUser(oAuthLoginUid = oAuthLoginUid, loginType = loginType)
        every { userRepository.save(any(User::class)) } returns expectedResult
        every { userRepository.existsByNickname(any(String::class)) } returns true andThen false

        // when
        val actualResult = sut.createNewUser(oAuthLoginUid, loginType)

        // then
        verify { userRepository.save(any(User::class)) }
        verify(exactly = 2) { userRepository.existsByNickname(any(String::class)) }
        confirmVerifiedEveryMocks()
        assertThat(actualResult).isEqualTo(expectedResult)
    }

    @Test
    fun `수정할 유저 정보가 주어지고, 유저 정보를 수정하면, 수정된 유저 정보가 반환된다`() {
        // given
        val userId = randomLong()
        val user = createUser(id = userId)
        val newNickname = randomString()
        val newGender = Gender.FEMALE
        val newBirthDate = randomLocalDate()
        val newInstagramId = randomString()
        val newProfileImage = createUploadFile()
        val uploadedFile = UploadedFile(randomString(), randomString())
        val expectedResult = user.update(newNickname, newGender, newBirthDate, newInstagramId, uploadedFile.url)

        every { userRepository.existsByNickname(newNickname) } returns false
        every { userRepository.getById(userId) } returns user
        every { fileStorageRepository.upload(newProfileImage, any(String::class)) } returns uploadedFile
        every { userRepository.update(expectedResult) } returns expectedResult

        // when
        val actualResult =
            sut.updateUserInfo(userId, newNickname, newGender, newBirthDate, newInstagramId, newProfileImage)

        // then
        verifyOrder {
            userRepository.existsByNickname(newNickname)
            userRepository.getById(userId)
            fileStorageRepository.upload(newProfileImage, any(String::class))
            userRepository.update(expectedResult)
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
        val existingNickname = randomString()
        every { userRepository.existsByNickname(existingNickname) } returns true

        // when
        val ex = catchThrowable { sut.updateUserInfo(userId, existingNickname, null, null, null, null) }

        // then
        verify { userRepository.existsByNickname(existingNickname) }
        confirmVerifiedEveryMocks()
        assertThat(ex).isInstanceOf(NicknameAlreadyExistsException::class.java)
    }

    @Test
    fun `수정할 유저의 생년월일이 주어지고, 유저 정보를 수정하면, 수정된 유저 정보가 반환된다`() {
        // given
        val userId = randomLong()
        val user = createUser(id = userId)
        val newBirthDate = randomLocalDate()
        val expectedResult = user.update(null, null, newBirthDate, null, null)

        every { userRepository.getById(userId) } returns user
        every { userRepository.update(expectedResult) } returns expectedResult
        // when
        val actualResult = sut.updateUserInfo(userId, null, null, newBirthDate, null, null)

        // then
        verifyOrder {
            userRepository.getById(userId)
            userRepository.update(expectedResult)
        }
        confirmVerifiedEveryMocks()
        assertThat(actualResult.id).isEqualTo(expectedResult.id)
        assertThat(actualResult.birthDate).isEqualTo(expectedResult.birthDate)
    }

    private fun confirmVerifiedEveryMocks() {
        confirmVerified(userRepository, fileStorageRepository)
    }
}
