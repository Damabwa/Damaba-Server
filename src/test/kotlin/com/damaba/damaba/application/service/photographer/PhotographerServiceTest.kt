package com.damaba.damaba.application.service.photographer

import com.damaba.damaba.application.port.inbound.photographer.CheckPhotographerNicknameExistenceUseCase
import com.damaba.damaba.application.port.inbound.photographer.RegisterPhotographerUseCase
import com.damaba.damaba.application.port.inbound.photographer.SavePhotographerUseCase
import com.damaba.damaba.application.port.outbound.photographer.CreatePhotographerPort
import com.damaba.damaba.application.port.outbound.photographer.CreateSavedPhotographerPort
import com.damaba.damaba.application.port.outbound.photographer.ExistsSavedPhotographerPort
import com.damaba.damaba.application.port.outbound.photographer.GetPhotographerPort
import com.damaba.damaba.application.port.outbound.user.CheckNicknameExistencePort
import com.damaba.damaba.application.port.outbound.user.GetUserPort
import com.damaba.damaba.domain.common.PhotographyType
import com.damaba.damaba.domain.photographer.Photographer
import com.damaba.damaba.domain.photographer.SavedPhotographer
import com.damaba.damaba.domain.photographer.exception.AlreadySavedPhotographerException
import com.damaba.damaba.domain.user.constant.Gender
import com.damaba.damaba.domain.user.constant.UserType
import com.damaba.damaba.domain.user.exception.NicknameAlreadyExistsException
import com.damaba.damaba.domain.user.exception.UserAlreadyRegisteredException
import com.damaba.damaba.util.RandomTestUtils.Companion.generateRandomSet
import com.damaba.damaba.util.RandomTestUtils.Companion.randomBoolean
import com.damaba.damaba.util.RandomTestUtils.Companion.randomLong
import com.damaba.damaba.util.RandomTestUtils.Companion.randomString
import com.damaba.damaba.util.fixture.FileFixture.createImage
import com.damaba.damaba.util.fixture.PhotographerFixture.createPhotographer
import com.damaba.damaba.util.fixture.RegionFixture.createRegion
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

class PhotographerServiceTest {
    private val getUserPort: GetUserPort = mockk()
    private val getPhotographerPort: GetPhotographerPort = mockk()
    private val checkNicknameExistencePort: CheckNicknameExistencePort = mockk()
    private val createPhotographerPort: CreatePhotographerPort = mockk()
    private val existsSavedPhotographerPort: ExistsSavedPhotographerPort = mockk()
    private val createSavedPhotographerPort: CreateSavedPhotographerPort = mockk()
    private val sut: PhotographerService = PhotographerService(
        getUserPort,
        getPhotographerPort,
        checkNicknameExistencePort,
        createPhotographerPort,
        existsSavedPhotographerPort,
        createSavedPhotographerPort,
    )

    private fun confirmVerifiedEveryMocks() {
        confirmVerified(
            getUserPort,
            getPhotographerPort,
            checkNicknameExistencePort,
            createPhotographerPort,
            existsSavedPhotographerPort,
            createSavedPhotographerPort,
        )
    }

    @Test
    fun `id가 주어지고, 주어진 id와 일치하는 사진작가를 단건 조회한다`() {
        // given
        val id = randomLong()
        val expectedResult = createPhotographer(id = id)
        every { getPhotographerPort.getById(id) } returns expectedResult

        // when
        val actualResult = sut.getPhotographer(id)

        // then
        verify { getPhotographerPort.getById(id) }
        confirmVerifiedEveryMocks()
        assertThat(actualResult).isEqualTo(expectedResult)
    }

    @Test
    fun `닉네임이 주어지고, 닉네임의 사용 여부를 조회한다`() {
        // given
        val nickname = randomString()
        val expectedResult = randomBoolean()
        every { checkNicknameExistencePort.doesNicknameExist(nickname) } returns expectedResult

        // when
        val actualResult = sut.doesNicknameExist(CheckPhotographerNicknameExistenceUseCase.Query(nickname))

        // then
        verify { checkNicknameExistencePort.doesNicknameExist(nickname) }
        confirmVerifiedEveryMocks()
        assertThat(actualResult).isEqualTo(expectedResult)
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
        every { createPhotographerPort.createIfUserExists(any(Photographer::class)) } returns expectedResult

        // when
        val actualResult = sut.register(command)

        // then
        verifyOrder {
            getUserPort.getById(userId)
            checkNicknameExistencePort.doesNicknameExist(command.nickname)
            createPhotographerPort.createIfUserExists(any(Photographer::class))
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

    @Test
    fun `사진작가를 저장한다`() {
        // given
        val command = SavePhotographerUseCase.Command(reqUserId = randomLong(), photographerId = randomLong())
        every {
            existsSavedPhotographerPort.existsByUserIdAndPhotographerId(command.reqUserId, command.photographerId)
        } returns false
        every {
            createSavedPhotographerPort.create(SavedPhotographer.create(command.reqUserId, command.photographerId))
        } just runs

        // when
        sut.savePhotographer(command)

        // then
        verify {
            existsSavedPhotographerPort.existsByUserIdAndPhotographerId(
                command.reqUserId,
                command.photographerId,
            )
        }
        verify {
            createSavedPhotographerPort.create(
                SavedPhotographer(
                    id = 0L,
                    userId = command.reqUserId,
                    photographerId = command.photographerId,
                ),
            )
        }
        confirmVerifiedEveryMocks()
    }

    @Test
    fun `사진작가를 저장한다, 만약 이미 저장한 사진작가라면 예외가 발생한다`() {
        // given
        val command = SavePhotographerUseCase.Command(reqUserId = randomLong(), photographerId = randomLong())
        every {
            existsSavedPhotographerPort.existsByUserIdAndPhotographerId(command.reqUserId, command.photographerId)
        } returns true

        // when
        val ex = catchThrowable { sut.savePhotographer(command) }

        // then
        verify {
            existsSavedPhotographerPort.existsByUserIdAndPhotographerId(
                command.reqUserId,
                command.photographerId,
            )
        }
        confirmVerifiedEveryMocks()
        assertThat(ex).isInstanceOf(AlreadySavedPhotographerException::class.java)
    }
}
