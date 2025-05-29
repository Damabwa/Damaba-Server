package com.damaba.damaba.application.photographer

import com.damaba.damaba.application.photographer.dto.ExistsPhotographerNicknameQuery
import com.damaba.damaba.application.photographer.dto.FindPhotographerListQuery
import com.damaba.damaba.application.photographer.dto.FindSavedPhotographerListQuery
import com.damaba.damaba.application.photographer.dto.RegisterPhotographerCommand
import com.damaba.damaba.application.photographer.dto.SavePhotographerCommand
import com.damaba.damaba.application.photographer.dto.UnsavePhotographerCommand
import com.damaba.damaba.application.photographer.dto.UpdatePhotographerPageCommand
import com.damaba.damaba.application.photographer.dto.UpdatePhotographerProfileCommand
import com.damaba.damaba.domain.common.Pagination
import com.damaba.damaba.domain.common.constant.PhotographyType
import com.damaba.damaba.domain.photographer.Photographer
import com.damaba.damaba.domain.photographer.PhotographerSave
import com.damaba.damaba.domain.photographer.constant.PhotographerSortType
import com.damaba.damaba.domain.photographer.exception.AlreadyPhotographerSaveException
import com.damaba.damaba.domain.photographer.exception.PhotographerSaveNotFoundException
import com.damaba.damaba.domain.region.RegionFilterCondition
import com.damaba.damaba.domain.user.constant.Gender
import com.damaba.damaba.domain.user.constant.UserType
import com.damaba.damaba.domain.user.exception.NicknameAlreadyExistsException
import com.damaba.damaba.domain.user.exception.UserAlreadyRegisteredException
import com.damaba.damaba.infrastructure.photographer.PhotographerRepository
import com.damaba.damaba.infrastructure.photographer.PhotographerSaveRepository
import com.damaba.damaba.infrastructure.promotion.PromotionRepository
import com.damaba.damaba.infrastructure.promotion.PromotionSaveRepository
import com.damaba.damaba.infrastructure.user.UserRepository
import com.damaba.damaba.util.RandomTestUtils.Companion.generateRandomList
import com.damaba.damaba.util.RandomTestUtils.Companion.generateRandomSet
import com.damaba.damaba.util.RandomTestUtils.Companion.randomBoolean
import com.damaba.damaba.util.RandomTestUtils.Companion.randomInt
import com.damaba.damaba.util.RandomTestUtils.Companion.randomLong
import com.damaba.damaba.util.RandomTestUtils.Companion.randomString
import com.damaba.damaba.util.fixture.FileFixture.createImage
import com.damaba.damaba.util.fixture.PhotographerFixture.createPhotographer
import com.damaba.damaba.util.fixture.PhotographerFixture.createPhotographerListItem
import com.damaba.damaba.util.fixture.PhotographerFixture.createPhotographerSave
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
import org.assertj.core.api.Assertions.assertThatIterable
import org.assertj.core.api.Assertions.catchThrowable
import kotlin.test.Test

class PhotographerServiceTest {
    private val userRepo: UserRepository = mockk()
    private val photographerRepo: PhotographerRepository = mockk()
    private val photographerSaveRepo: PhotographerSaveRepository = mockk()
    private val promotionRepo: PromotionRepository = mockk()
    private val promotionSaveRepo: PromotionSaveRepository = mockk()
    private val sut: PhotographerService = PhotographerService(
        userRepo,
        photographerRepo,
        photographerSaveRepo,
        promotionRepo,
        promotionSaveRepo,
    )

    private fun confirmVerifiedEveryMocks() {
        confirmVerified(
            userRepo,
            photographerRepo,
            photographerSaveRepo,
            promotionRepo,
            promotionSaveRepo,
        )
    }

    @Test
    fun `id가 주어지고, 주어진 id와 일치하는 사진작가를 단건 조회한다`() {
        // given
        val id = randomLong()
        val expectedResult = createPhotographer(id = id)
        every { photographerRepo.getById(id) } returns expectedResult

        // when
        val actualResult = sut.getPhotographer(id)

        // then
        verify { photographerRepo.getById(id) }
        confirmVerifiedEveryMocks()
        assertThat(actualResult).isEqualTo(expectedResult)
    }

    @Test
    fun `사진작가 리스트를 조회한다`() {
        // given
        val query = FindPhotographerListQuery(
            requestUserId = null,
            regions = setOf(RegionFilterCondition("서울", "강남구"), RegionFilterCondition("대전", "중구")),
            photographyTypes = setOf(PhotographyType.PROFILE, PhotographyType.SELF),
            searchKeyword = null,
            sort = PhotographerSortType.LATEST,
            page = randomInt(min = 1),
            pageSize = randomInt(min = 5, max = 10),
        )
        val expectedResult = Pagination(
            items = generateRandomList(maxSize = query.pageSize) { createPhotographerListItem(profileImage = createImage()) },
            page = query.page,
            pageSize = query.pageSize,
            totalPage = 10,
        )
        every {
            photographerRepo.findPhotographerList(
                requestUserId = query.requestUserId,
                regions = query.regions,
                photographyTypes = query.photographyTypes,
                searchKeyword = null,
                sort = query.sort,
                page = query.page,
                pageSize = query.pageSize,
            )
        } returns expectedResult

        // when
        val actualResult = sut.findPhotographerList(query)

        // then
        verify {
            photographerRepo.findPhotographerList(
                requestUserId = query.requestUserId,
                regions = query.regions,
                photographyTypes = query.photographyTypes,
                searchKeyword = null,
                sort = query.sort,
                page = query.page,
                pageSize = query.pageSize,
            )
        }
        confirmVerifiedEveryMocks()
        assertThat(actualResult).isEqualTo(expectedResult)
        assertThatIterable(actualResult.items).isEqualTo(expectedResult.items)
    }

    @Test
    fun `사진작가 리스트를 조회한다_`() {
        // given
        val query = FindPhotographerListQuery(
            requestUserId = null,
            regions = setOf(RegionFilterCondition("서울", "강남구"), RegionFilterCondition("대전", "중구")),
            photographyTypes = setOf(PhotographyType.PROFILE, PhotographyType.SELF),
            searchKeyword = null,
            sort = PhotographerSortType.LATEST,
            page = randomInt(min = 1),
            pageSize = randomInt(min = 5, max = 10),
        )
        val expectedResult = Pagination(
            items = generateRandomList(maxSize = query.pageSize) { createPhotographerListItem(profileImage = null) },
            page = query.page,
            pageSize = query.pageSize,
            totalPage = 10,
        )
        every {
            photographerRepo.findPhotographerList(
                requestUserId = query.requestUserId,
                regions = query.regions,
                photographyTypes = query.photographyTypes,
                searchKeyword = null,
                sort = query.sort,
                page = query.page,
                pageSize = query.pageSize,
            )
        } returns expectedResult

        // when
        val actualResult = sut.findPhotographerList(query)

        // then
        verify {
            photographerRepo.findPhotographerList(
                requestUserId = query.requestUserId,
                regions = query.regions,
                photographyTypes = query.photographyTypes,
                searchKeyword = null,
                sort = query.sort,
                page = query.page,
                pageSize = query.pageSize,
            )
        }
        confirmVerifiedEveryMocks()
        assertThat(actualResult).isEqualTo(expectedResult)
        assertThatIterable(actualResult.items).isEqualTo(expectedResult.items)
    }

    @Test
    fun `저장된 사진작가 리스트를 조회한다`() {
        // given
        val requestUser = createUser()
        val query = FindSavedPhotographerListQuery(
            requestUserId = requestUser.id,
            page = randomInt(min = 1),
            pageSize = randomInt(min = 5, max = 10),
        )
        val expectedResult = Pagination(
            items = generateRandomList(maxSize = query.pageSize) { createPhotographerListItem(profileImage = null) },
            page = query.page,
            pageSize = query.pageSize,
            totalPage = 10,
        )
        every {
            photographerRepo.findSavedPhotographerList(
                requestUserId = query.requestUserId,
                page = query.page,
                pageSize = query.pageSize,
            )
        } returns expectedResult

        // when
        val actualResult = sut.findSavedPhotographerList(query)

        // then
        verify {
            photographerRepo.findSavedPhotographerList(
                requestUserId = query.requestUserId,
                page = query.page,
                pageSize = query.pageSize,
            )
        }
        confirmVerifiedEveryMocks()
        assertThat(actualResult).isEqualTo(expectedResult)
        assertThatIterable(actualResult.items).isEqualTo(expectedResult.items)
    }

    @Test
    fun `닉네임이 주어지고, 닉네임의 사용 여부를 조회한다`() {
        // given
        val nickname = randomString()
        val expectedResult = randomBoolean()
        every { userRepo.existsNickname(nickname) } returns expectedResult

        // when
        val actualResult = sut.existsNickname(ExistsPhotographerNicknameQuery(nickname))

        // then
        verify { userRepo.existsNickname(nickname) }
        confirmVerifiedEveryMocks()
        assertThat(actualResult).isEqualTo(expectedResult)
    }

    @Test
    fun `사진작가 등록 정보가 주어지고, 사진작가를 등록한다`() {
        // given
        val userId = randomLong()
        val unregisteredUser = createUser(id = userId, type = UserType.UNDEFINED)
        val command = RegisterPhotographerCommand(
            userId = userId,
            nickname = randomString(len = 10),
            gender = Gender.FEMALE,
            instagramId = randomString(len = 15),
            profileImage = createImage(),
            mainPhotographyTypes = setOf(PhotographyType.PROFILE, PhotographyType.SELF),
            activeRegions = generateRandomSet(maxSize = 3) { createRegion() },
        )
        val expectedResult = createPhotographer(id = userId)
        every { userRepo.getById(userId) } returns unregisteredUser
        every { userRepo.existsNickname(command.nickname) } returns false
        every { photographerRepo.createIfUserExists(any(Photographer::class)) } returns expectedResult

        // when
        val actualResult = sut.register(command)

        // then
        verifyOrder {
            userRepo.getById(userId)
            userRepo.existsNickname(command.nickname)
            photographerRepo.createIfUserExists(any(Photographer::class))
        }
        confirmVerifiedEveryMocks()
        assertThat(actualResult).isEqualTo(expectedResult)
    }

    @Test
    fun `이미 등록된 유저가, 사진작가로 등록하면, 예외가 발생한다`() {
        // given
        val userId = randomLong()
        val registeredUser = createUser(id = userId, type = UserType.PHOTOGRAPHER)
        val command = RegisterPhotographerCommand(
            userId = userId,
            nickname = randomString(len = 10),
            gender = Gender.FEMALE,
            instagramId = null,
            profileImage = createImage(),
            mainPhotographyTypes = setOf(PhotographyType.PROFILE, PhotographyType.SELF),
            activeRegions = generateRandomSet(maxSize = 3) { createRegion() },
        )
        every { userRepo.getById(userId) } returns registeredUser

        // when
        val ex = catchThrowable { sut.register(command) }

        // then
        verify { userRepo.getById(userId) }
        confirmVerifiedEveryMocks()
        assertThat(ex).isInstanceOf(UserAlreadyRegisteredException::class.java)
    }

    @Test
    fun `다른 유저가 사용중인 닉네임이 등록 정보가 주어지고, 사진작가를 등록하면, 예외가 발생한다`() {
        // given
        val userId = randomLong()
        val unregisteredUser = createUser(id = userId, type = UserType.UNDEFINED)
        val command = RegisterPhotographerCommand(
            userId = userId,
            nickname = randomString(len = 10),
            gender = Gender.FEMALE,
            instagramId = null,
            profileImage = createImage(),
            mainPhotographyTypes = setOf(PhotographyType.PROFILE, PhotographyType.SELF),
            activeRegions = generateRandomSet(maxSize = 3) { createRegion() },
        )
        every { userRepo.getById(userId) } returns unregisteredUser
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
    fun `사진작가를 저장한다`() {
        // given
        val command = SavePhotographerCommand(requestUserId = randomLong(), photographerId = randomLong())
        every {
            photographerSaveRepo.existsByUserIdAndPhotographerId(command.requestUserId, command.photographerId)
        } returns false
        every {
            photographerSaveRepo.create(PhotographerSave.create(command.requestUserId, command.photographerId))
        } just runs

        // when
        sut.savePhotographer(command)

        // then
        verify {
            photographerSaveRepo.existsByUserIdAndPhotographerId(
                command.requestUserId,
                command.photographerId,
            )
        }
        verify {
            photographerSaveRepo.create(
                PhotographerSave(
                    id = 0L,
                    userId = command.requestUserId,
                    photographerId = command.photographerId,
                ),
            )
        }
        confirmVerifiedEveryMocks()
    }

    @Test
    fun `사진작가를 저장한다, 만약 이미 저장한 사진작가라면 예외가 발생한다`() {
        // given
        val command = SavePhotographerCommand(requestUserId = randomLong(), photographerId = randomLong())
        every {
            photographerSaveRepo.existsByUserIdAndPhotographerId(command.requestUserId, command.photographerId)
        } returns true

        // when
        val ex = catchThrowable { sut.savePhotographer(command) }

        // then
        verify {
            photographerSaveRepo.existsByUserIdAndPhotographerId(
                command.requestUserId,
                command.photographerId,
            )
        }
        confirmVerifiedEveryMocks()
        assertThat(ex).isInstanceOf(AlreadyPhotographerSaveException::class.java)
    }

    @Test
    fun `작가 프로필을 수정하면, 수정된 작가 정보가 반환된다`() {
        // given
        val photographerId = randomLong()
        val originalPhotographer = createPhotographer(
            id = photographerId,
            mainPhotographyTypes = setOf(PhotographyType.PROFILE),
        )
        val command = UpdatePhotographerProfileCommand(
            photographerId = photographerId,
            nickname = originalPhotographer.nickname,
            profileImage = originalPhotographer.profileImage,
            mainPhotographyTypes = setOf(PhotographyType.SNAP),
            activeRegions = generateRandomSet(maxSize = 3) { createRegion() },
        )
        val expectedResult = createPhotographer(
            id = photographerId,
            nickname = command.nickname,
            profileImage = command.profileImage,
            mainPhotographyTypes = command.mainPhotographyTypes,
            activeRegions = command.activeRegions,
        )
        every { photographerRepo.getById(photographerId) } returns originalPhotographer
        every { photographerRepo.update(expectedResult) } returns expectedResult

        // when
        val result = sut.updatePhotographerProfile(command)

        // then
        verifyOrder {
            photographerRepo.getById(photographerId)
            photographerRepo.update(expectedResult)
        }
        confirmVerifiedEveryMocks()
        assertThat(result).isEqualTo(expectedResult)
    }

    @Test
    fun `(기존 프로필 이미지가 null인 경우) 작가 프로필을 수정하면, 수정된 작가 정보가 반환된다`() {
        // given
        val photographerId = randomLong()
        val originalPhotographer = createPhotographer(
            id = photographerId,
            mainPhotographyTypes = setOf(PhotographyType.PROFILE),
            profileImage = null,
        )
        val command = UpdatePhotographerProfileCommand(
            photographerId = photographerId,
            nickname = originalPhotographer.nickname,
            profileImage = originalPhotographer.profileImage,
            mainPhotographyTypes = setOf(PhotographyType.SNAP),
            activeRegions = generateRandomSet(maxSize = 3) { createRegion() },
        )
        val expectedResult = createPhotographer(
            id = photographerId,
            nickname = command.nickname,
            profileImage = command.profileImage,
            mainPhotographyTypes = command.mainPhotographyTypes,
            activeRegions = command.activeRegions,
        )
        every { photographerRepo.getById(photographerId) } returns originalPhotographer
        every { photographerRepo.update(expectedResult) } returns expectedResult

        // when
        val result = sut.updatePhotographerProfile(command)

        // then
        verifyOrder {
            photographerRepo.getById(photographerId)
            photographerRepo.update(expectedResult)
        }
        confirmVerifiedEveryMocks()
        assertThat(result).isEqualTo(expectedResult)
    }

    @Test
    fun `작가 프로필을 수정한다, 만약 프로필 이미지가 변경되었다면 기존 이미지를 삭제한다`() {
        // given
        val photographerId = randomLong()
        val originalPhotographer = createPhotographer(
            id = photographerId,
            mainPhotographyTypes = setOf(PhotographyType.PROFILE),
        )
        val originalProfileImageUrl = originalPhotographer.profileImage!!.url
        val command = UpdatePhotographerProfileCommand(
            photographerId = photographerId,
            nickname = randomString(len = 10),
            profileImage = createImage(name = "newImage", url = "https://new-image.jpg"),
            mainPhotographyTypes = setOf(PhotographyType.SNAP),
            activeRegions = generateRandomSet(maxSize = 3) { createRegion() },
        )
        val expectedResult = createPhotographer(
            id = photographerId,
            nickname = command.nickname,
            profileImage = command.profileImage,
            mainPhotographyTypes = command.mainPhotographyTypes,
            activeRegions = command.activeRegions,
        )
        every { photographerRepo.getById(photographerId) } returns originalPhotographer
        every { userRepo.existsNickname(command.nickname) } returns false
        every { userRepo.deleteProfileImageByUrl(originalProfileImageUrl) } just runs
        every { photographerRepo.update(expectedResult) } returns expectedResult

        // when
        val result = sut.updatePhotographerProfile(command)

        // then
        verifyOrder {
            photographerRepo.getById(photographerId)
            userRepo.existsNickname(command.nickname)
            userRepo.deleteProfileImageByUrl(originalProfileImageUrl)
            photographerRepo.update(expectedResult)
        }
        confirmVerifiedEveryMocks()
        assertThat(result).isEqualTo(expectedResult)
    }

    @Test
    fun `작가 프로필을 수정한다, 만약 변경하려는 닉네임이 이미 사용중이라면 예외가 발생한다`() {
        // given
        val photographerId = randomLong()
        val originalPhotographer = createPhotographer(
            id = photographerId,
            mainPhotographyTypes = setOf(PhotographyType.PROFILE),
        )
        val command = UpdatePhotographerProfileCommand(
            photographerId = photographerId,
            nickname = randomString(len = 10),
            profileImage = createImage(name = "newImage", url = "https://new-image.jpg"),
            mainPhotographyTypes = setOf(PhotographyType.SNAP),
            activeRegions = generateRandomSet(maxSize = 3) { createRegion() },
        )
        every { photographerRepo.getById(photographerId) } returns originalPhotographer
        every { userRepo.existsNickname(command.nickname) } returns true

        // when
        val ex = catchThrowable { sut.updatePhotographerProfile(command) }

        // then
        verifyOrder {
            photographerRepo.getById(photographerId)
            userRepo.existsNickname(command.nickname)
        }
        confirmVerifiedEveryMocks()
        assertThat(ex).isInstanceOf(NicknameAlreadyExistsException::class.java)
    }

    @Test
    fun `작가 페이지를 수정하면, 수정된 작가가 반환된다`() {
        val photographerId = randomLong()
        val originalPhotographer = createPhotographer(
            id = photographerId,
            mainPhotographyTypes = setOf(PhotographyType.PROFILE),
        )
        val command = UpdatePhotographerPageCommand(
            photographerId = photographerId,
            portfolio = generateRandomList(maxSize = 3) { createImage() },
            address = null,
            instagramId = null,
            contactLink = null,
            description = randomString(len = randomInt(min = 1, max = 300)),
        )
        val expectedResult = createPhotographer(
            id = photographerId,
            portfolio = command.portfolio,
            address = command.address,
            instagramId = command.instagramId,
            contactLink = command.contactLink,
            description = command.description,
        )
        every { photographerRepo.getById(photographerId) } returns originalPhotographer
        every { photographerRepo.update(expectedResult) } returns expectedResult

        // when
        val result = sut.updatePhotographerPage(command)

        // then
        verifyOrder {
            photographerRepo.getById(photographerId)
            photographerRepo.update(expectedResult)
        }
        confirmVerifiedEveryMocks()
        assertThat(result).isEqualTo(expectedResult)
    }

    @Test
    fun `사진작가를 저장 해제한다`() {
        // given
        val command = UnsavePhotographerCommand(requestUserId = randomLong(), photographerId = randomLong())
        val photographerSave = createPhotographerSave()
        every {
            photographerSaveRepo.findByUserIdAndPhotographerId(command.requestUserId, command.photographerId)
        } returns photographerSave
        every { photographerSaveRepo.delete(photographerSave) } just runs

        // when
        sut.unsavePhotographer(command)

        // then
        verify { photographerSaveRepo.findByUserIdAndPhotographerId(command.requestUserId, command.photographerId) }
        verify { photographerSaveRepo.delete(photographerSave) }
        confirmVerifiedEveryMocks()
    }

    @Test
    fun `사진작가를 저장 해제한다, 만약 저장한 적 없는 사진작가라면 예외가 발생한다`() {
        // given
        val command = UnsavePhotographerCommand(requestUserId = randomLong(), photographerId = randomLong())
        every {
            photographerSaveRepo.findByUserIdAndPhotographerId(command.requestUserId, command.photographerId)
        } returns null

        // when
        val ex = catchThrowable { sut.unsavePhotographer(command) }

        // then
        verify { photographerSaveRepo.findByUserIdAndPhotographerId(command.requestUserId, command.photographerId) }
        confirmVerifiedEveryMocks()
        assertThat(ex).isInstanceOf(PhotographerSaveNotFoundException::class.java)
    }
}
