package com.damaba.damaba.application.service.photographer

import com.damaba.damaba.application.port.inbound.photographer.RegisterPhotographerUseCase
import com.damaba.damaba.application.port.outbound.photographer.SavePhotographerPort
import com.damaba.damaba.domain.common.PhotographyType
import com.damaba.damaba.domain.photographer.Photographer
import com.damaba.damaba.util.RandomTestUtils.Companion.generateRandomSet
import com.damaba.damaba.util.RandomTestUtils.Companion.randomLong
import com.damaba.damaba.util.RandomTestUtils.Companion.randomString
import com.damaba.damaba.util.TestFixture.createImage
import com.damaba.damaba.util.TestFixture.createPhotographer
import com.damaba.damaba.util.TestFixture.createRegion
import com.damaba.damaba.util.TestFixture.createUser
import com.damaba.user.application.port.outbound.user.CheckNicknameExistencePort
import com.damaba.user.application.port.outbound.user.GetUserPort
import com.damaba.user.domain.user.constant.Gender
import com.damaba.user.domain.user.constant.UserType
import com.damaba.user.domain.user.exception.NicknameAlreadyExistsException
import com.damaba.user.domain.user.exception.UserAlreadyRegisteredException
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import kotlin.test.Test

class PhotographerServiceTest {
    private val getUserPort: GetUserPort = mockk()
    private val checkNicknameExistencePort: CheckNicknameExistencePort = mockk()
    private val savePhotographerPort: SavePhotographerPort = mockk()
    private val sut: PhotographerService = PhotographerService(
        getUserPort,
        checkNicknameExistencePort,
        savePhotographerPort,
    )

    private fun confirmVerifiedEveryMocks() {
        confirmVerified(
            getUserPort,
            checkNicknameExistencePort,
            savePhotographerPort,
        )
    }

    @Test
    fun `사진작가 등록 정보가 주어지고, 사진작가를 등록한다`() {
        // given
        val userId = randomLong()
        val unregisteredUser = createUser(id = userId, type = UserType.UNDEFINED)
        val command = RegisterPhotographerUseCase.Command(
            userId = userId,
            nickname = randomString(len = 10),
            gender = Gender.FEMALE,
            instagramId = randomString(len = 15),
            profileImage = createImage(),
            mainPhotographyTypes = setOf(PhotographyType.PROFILE, PhotographyType.SELF),
            activeRegions = generateRandomSet(maxSize = 3) { createRegion() },
        )
        val expectedResult = createPhotographer(id = userId)
        every { getUserPort.getById(userId) } returns unregisteredUser
        every { checkNicknameExistencePort.doesNicknameExist(command.nickname) } returns false
        every { savePhotographerPort.saveIfUserExists(any(Photographer::class)) } returns expectedResult

        // when
        val actualResult = sut.register(command)

        // then
        verifyOrder {
            getUserPort.getById(userId)
            checkNicknameExistencePort.doesNicknameExist(command.nickname)
            savePhotographerPort.saveIfUserExists(any(Photographer::class))
        }
        confirmVerifiedEveryMocks()
        assertThat(actualResult).isEqualTo(expectedResult)
    }

    @Test
    fun `이미 등록된 유저가, 사진작가로 등록하면, 예외가 발생한다`() {
        // given
        val userId = randomLong()
        val registeredUser = createUser(id = userId, type = UserType.PHOTOGRAPHER)
        val command = RegisterPhotographerUseCase.Command(
            userId = userId,
            nickname = randomString(len = 10),
            gender = Gender.FEMALE,
            instagramId = null,
            profileImage = createImage(),
            mainPhotographyTypes = setOf(PhotographyType.PROFILE, PhotographyType.SELF),
            activeRegions = generateRandomSet(maxSize = 3) { createRegion() },
        )
        every { getUserPort.getById(userId) } returns registeredUser

        // when
        val ex = catchThrowable { sut.register(command) }

        // then
        verify { getUserPort.getById(userId) }
        confirmVerifiedEveryMocks()
        assertThat(ex).isInstanceOf(UserAlreadyRegisteredException::class.java)
    }

    @Test
    fun `다른 유저가 사용중인 닉네임이 등록 정보가 주어지고, 사진작가를 등록하면, 예외가 발생한다`() {
        // given
        val userId = randomLong()
        val unregisteredUser = createUser(id = userId, type = UserType.UNDEFINED)
        val command = RegisterPhotographerUseCase.Command(
            userId = userId,
            nickname = randomString(len = 10),
            gender = Gender.FEMALE,
            instagramId = null,
            profileImage = createImage(),
            mainPhotographyTypes = setOf(PhotographyType.PROFILE, PhotographyType.SELF),
            activeRegions = generateRandomSet(maxSize = 3) { createRegion() },
        )
        every { getUserPort.getById(userId) } returns unregisteredUser
        every { checkNicknameExistencePort.doesNicknameExist(command.nickname) } returns true

        // when
        val ex = catchThrowable { sut.register(command) }

        // then
        verifyOrder {
            getUserPort.getById(userId)
            checkNicknameExistencePort.doesNicknameExist(command.nickname)
        }
        confirmVerifiedEveryMocks()
        assertThat(ex).isInstanceOf(NicknameAlreadyExistsException::class.java)
    }
}
