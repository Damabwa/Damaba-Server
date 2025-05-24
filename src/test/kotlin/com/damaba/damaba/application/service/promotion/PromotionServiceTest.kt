package com.damaba.damaba.application.service.promotion

import com.damaba.damaba.application.port.inbound.promotion.DeletePromotionUseCase
import com.damaba.damaba.application.port.inbound.promotion.FindPromotionListUseCase
import com.damaba.damaba.application.port.inbound.promotion.FindSavedPromotionListUseCase
import com.damaba.damaba.application.port.inbound.promotion.GetPromotionDetailUseCase
import com.damaba.damaba.application.port.inbound.promotion.PostPromotionUseCase
import com.damaba.damaba.application.port.inbound.promotion.SavePromotionUseCase
import com.damaba.damaba.application.port.inbound.promotion.UnsavePromotionUseCase
import com.damaba.damaba.application.port.inbound.promotion.UpdatePromotionUseCase
import com.damaba.damaba.domain.common.Pagination
import com.damaba.damaba.domain.common.constant.PhotographyType
import com.damaba.damaba.domain.promotion.Promotion
import com.damaba.damaba.domain.promotion.PromotionSave
import com.damaba.damaba.domain.promotion.constant.PromotionProgressStatus
import com.damaba.damaba.domain.promotion.constant.PromotionSortType
import com.damaba.damaba.domain.promotion.constant.PromotionType
import com.damaba.damaba.domain.promotion.exception.AlreadyPromotionSaveException
import com.damaba.damaba.domain.promotion.exception.PromotionDeletePermissionDeniedException
import com.damaba.damaba.domain.promotion.exception.PromotionUpdatePermissionDeniedException
import com.damaba.damaba.domain.region.RegionFilterCondition
import com.damaba.damaba.domain.user.constant.UserRoleType
import com.damaba.damaba.infrastructure.promotion.PromotionCoreRepository
import com.damaba.damaba.infrastructure.promotion.PromotionRepository
import com.damaba.damaba.infrastructure.promotion.PromotionSaveRepository
import com.damaba.damaba.infrastructure.user.UserCoreRepository
import com.damaba.damaba.infrastructure.user.UserRepository
import com.damaba.damaba.util.RandomTestUtils.Companion.generateRandomList
import com.damaba.damaba.util.RandomTestUtils.Companion.generateRandomSet
import com.damaba.damaba.util.RandomTestUtils.Companion.randomBoolean
import com.damaba.damaba.util.RandomTestUtils.Companion.randomInt
import com.damaba.damaba.util.RandomTestUtils.Companion.randomLocalDate
import com.damaba.damaba.util.RandomTestUtils.Companion.randomLong
import com.damaba.damaba.util.RandomTestUtils.Companion.randomString
import com.damaba.damaba.util.RandomTestUtils.Companion.randomUrl
import com.damaba.damaba.util.fixture.FileFixture.createImage
import com.damaba.damaba.util.fixture.PromotionFixture.createPromotion
import com.damaba.damaba.util.fixture.PromotionFixture.createPromotionListItem
import com.damaba.damaba.util.fixture.PromotionFixture.createPromotionSave
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
import org.junit.jupiter.api.Nested
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.util.concurrent.CompletableFuture
import kotlin.test.Test

class PromotionServiceTest {
    @Nested
    inner class UnitTest {
        private val userRepo: UserRepository = mockk()
        private val promotionRepo: PromotionRepository = mockk()
        private val promotionSaveRepo: PromotionSaveRepository = mockk()

        private val sut: PromotionService = PromotionService(
            userRepo,
            promotionRepo,
            promotionSaveRepo,
        )

        private fun confirmVerifiedEveryMocks() {
            confirmVerified(
                userRepo,
                promotionRepo,
                promotionSaveRepo,
            )
        }

        @Test
        fun `프로모션 id가 주어지고, 일치하는 프로모션을 단건 조회한다`() {
            // given
            val promotionId = randomLong()
            val expectedResult = createPromotion()
            every { promotionRepo.getById(promotionId) } returns expectedResult

            // when
            val actualResult = sut.getPromotion(promotionId)

            // then
            verify { promotionRepo.getById(promotionId) }
            confirmVerifiedEveryMocks()
            assertThat(actualResult).isEqualTo(expectedResult)
        }

        @Test
        fun `프로모션을 상세 조회하면, 조회수가 1 증가하고 프로모션 상세 정보가 반환된다`() {
            // given
            val requestUserId = randomLong()
            val promotionId = randomLong()
            val originalViewCount = randomLong()
            val promotion = createPromotion(id = promotionId, viewCount = originalViewCount)
            val author = createUser(id = promotion.authorId!!)
            val expectedSaveCount = randomLong()
            val expectedIsSaved = randomBoolean()
            every { promotionRepo.getById(promotionId) } returns promotion
            every { promotionRepo.update(any(Promotion::class)) } returns promotion
            every { userRepo.getById(promotion.authorId!!) } returns author
            every { promotionSaveRepo.countByPromotionId(promotionId) } returns expectedSaveCount
            every {
                promotionSaveRepo.existsByUserIdAndPromotionId(requestUserId, promotionId)
            } returns expectedIsSaved

            // when
            val result = sut.getPromotionDetail(GetPromotionDetailUseCase.Query(requestUserId, promotionId))

            // then
            verify { promotionRepo.getById(promotionId) }
            verify { promotionRepo.update(any(Promotion::class)) }
            verify { userRepo.getById(promotion.authorId!!) }
            verify { promotionSaveRepo.countByPromotionId(promotionId) }
            verify { promotionSaveRepo.existsByUserIdAndPromotionId(requestUserId, promotionId) }
            confirmVerifiedEveryMocks()
            assertThat(result.id).isEqualTo(promotion.id)
            assertThat(result.viewCount).isEqualTo(originalViewCount + 1)
            assertThat(result.saveCount).isEqualTo(expectedSaveCount)
            assertThat(result.isSaved).isEqualTo(expectedIsSaved)
        }

        @Test
        fun `프로모션을 상세 조회하면, 조회수가 증가하고 상세 정보가 반환된다, 요청자 정보가 없다면 저장 여부는 false로 설정된다`() {
            // given
            val requestUserId = null
            val promotionId = randomLong()
            val originalViewCount = randomLong()
            val promotion = createPromotion(id = promotionId, viewCount = originalViewCount)
            val author = createUser(id = promotion.authorId!!)
            val expectedSaveCount = randomLong()
            every { promotionRepo.getById(promotionId) } returns promotion
            every { promotionRepo.update(any(Promotion::class)) } returns promotion
            every { userRepo.getById(promotion.authorId!!) } returns author
            every { promotionSaveRepo.countByPromotionId(promotionId) } returns expectedSaveCount

            // when
            val result = sut.getPromotionDetail(GetPromotionDetailUseCase.Query(requestUserId, promotionId))

            // then
            verify { promotionRepo.getById(promotionId) }
            verify { promotionRepo.update(any(Promotion::class)) }
            verify { userRepo.getById(promotion.authorId!!) }
            verify { promotionSaveRepo.countByPromotionId(promotionId) }
            confirmVerifiedEveryMocks()
            assertThat(result.id).isEqualTo(promotion.id)
            assertThat(result.viewCount).isEqualTo(originalViewCount + 1)
            assertThat(result.saveCount).isEqualTo(expectedSaveCount)
            assertThat(result.isSaved).isFalse()
        }

        @Test
        fun `프로모션을 상세 조회하면, 조회수가 증가하고 프로모션 상세 정보가 반환된다, 작성자 정보가 존재하지 않는다면 null로 설정된다`() {
            // given
            val requestUserId = randomLong()
            val promotionId = randomLong()
            val originalViewCount = randomLong()
            val promotion = createPromotion(id = promotionId, authorId = null, viewCount = originalViewCount)
            val expectedSaveCount = randomLong()
            val expectedIsSaved = randomBoolean()
            every { promotionRepo.getById(promotionId) } returns promotion
            every { promotionRepo.update(any(Promotion::class)) } returns promotion
            every { promotionSaveRepo.countByPromotionId(promotionId) } returns expectedSaveCount
            every {
                promotionSaveRepo.existsByUserIdAndPromotionId(requestUserId, promotionId)
            } returns expectedIsSaved

            // when
            val result = sut.getPromotionDetail(GetPromotionDetailUseCase.Query(requestUserId, promotionId))

            // then
            verify { promotionRepo.getById(promotionId) }
            verify { promotionRepo.update(any(Promotion::class)) }
            verify { promotionSaveRepo.countByPromotionId(promotionId) }
            verify { promotionSaveRepo.existsByUserIdAndPromotionId(requestUserId, promotionId) }
            confirmVerifiedEveryMocks()
            assertThat(result.id).isEqualTo(promotion.id)
            assertThat(result.author).isNull()
            assertThat(result.viewCount).isEqualTo(originalViewCount + 1)
            assertThat(result.saveCount).isEqualTo(expectedSaveCount)
            assertThat(result.isSaved).isEqualTo(expectedIsSaved)
        }

        @Test
        fun `프로모션 리스트를 조회한다`() {
            // given
            val query = FindSavedPromotionListUseCase.Query(
                requestUserId = randomLong(),
                page = 1,
                pageSize = randomInt(min = 5, max = 10),
            )
            val expectedResult = Pagination(
                items = generateRandomList(maxSize = query.pageSize) { createPromotionListItem() },
                page = query.page,
                pageSize = query.pageSize,
                totalPage = 1,
            )
            every {
                promotionRepo.findSavedPromotionList(
                    requestUserId = query.requestUserId,
                    page = query.page,
                    pageSize = query.pageSize,
                )
            } returns expectedResult

            // when
            val actualResult = sut.findSavedPromotionList(query)

            // then
            verify {
                promotionRepo.findSavedPromotionList(
                    requestUserId = query.requestUserId,
                    page = query.page,
                    pageSize = query.pageSize,
                )
            }
            confirmVerifiedEveryMocks()
            assertThat(actualResult).isEqualTo(expectedResult)
        }

        @Test
        fun `저장된 프로모션 리스트를 조회한다`() {
            // given
            val query = FindPromotionListUseCase.Query(
                reqUserId = null,
                type = PromotionType.FREE,
                progressStatus = PromotionProgressStatus.ONGOING,
                regions = setOf(RegionFilterCondition("서울", "강남구"), RegionFilterCondition("대전", "중구")),
                photographyTypes = setOf(PhotographyType.PROFILE, PhotographyType.SELF),
                sortType = PromotionSortType.LATEST,
                page = 1,
                pageSize = randomInt(min = 5, max = 10),
            )
            val expectedResult = Pagination(
                items = generateRandomList(maxSize = query.pageSize) { createPromotionListItem() },
                page = query.page,
                pageSize = query.pageSize,
                totalPage = 1,
            )
            every {
                promotionRepo.findPromotionList(
                    requestUserId = query.reqUserId,
                    type = query.type,
                    progressStatus = query.progressStatus,
                    regions = query.regions,
                    photographyTypes = query.photographyTypes,
                    sortType = query.sortType,
                    page = query.page,
                    pageSize = query.pageSize,
                )
            } returns expectedResult

            // when
            val actualResult = sut.findPromotionList(query)

            // then
            verify {
                promotionRepo.findPromotionList(
                    requestUserId = query.reqUserId,
                    type = query.type,
                    progressStatus = query.progressStatus,
                    regions = query.regions,
                    photographyTypes = query.photographyTypes,
                    sortType = query.sortType,
                    page = query.page,
                    pageSize = query.pageSize,
                )
            }
            confirmVerifiedEveryMocks()
            assertThat(actualResult).isEqualTo(expectedResult)
        }

        @Test
        fun `등록할 이벤트 프로모션 정보들이 주어지고, 주어진 정보로 이벤트 프로모션을 생성 및 등록한다`() {
            // given
            val command = createPostPromotionCommand()
            val expectedResult = createPromotion()
            every { promotionRepo.create(any(Promotion::class)) } returns expectedResult

            // when
            val actualResult = sut.postPromotion(command)

            // then
            verify { promotionRepo.create(any(Promotion::class)) }
            confirmVerifiedEveryMocks()
            assertThat(actualResult).isEqualTo(expectedResult)
            assertThatIterable(actualResult.images).isEqualTo(expectedResult.images)
            assertThatIterable(actualResult.activeRegions).isEqualTo(expectedResult.activeRegions)
            assertThatIterable(actualResult.hashtags).isEqualTo(expectedResult.hashtags)
        }

        @Test
        fun `프로모션을 저장한다`() {
            // given
            val userId = randomLong()
            val promotionId = randomLong()
            every { promotionSaveRepo.existsByUserIdAndPromotionId(userId, promotionId) } returns false
            every { promotionSaveRepo.create(any(PromotionSave::class)) } just runs

            // when
            sut.savePromotion(SavePromotionUseCase.Command(userId, promotionId))

            // then
            verify { promotionSaveRepo.existsByUserIdAndPromotionId(userId, promotionId) }
            verify { promotionSaveRepo.create(any(PromotionSave::class)) }
            confirmVerifiedEveryMocks()
        }

        @Test
        fun `프로모션을 저장한다, 이미 저장된 프로모션이라면 예외가 발생한다`() {
            // given
            val userId = randomLong()
            val promotionId = randomLong()
            every { promotionSaveRepo.existsByUserIdAndPromotionId(userId, promotionId) } returns true

            // when
            val ex = catchThrowable { sut.savePromotion(SavePromotionUseCase.Command(userId, promotionId)) }

            // then
            verify { promotionSaveRepo.existsByUserIdAndPromotionId(userId, promotionId) }
            confirmVerifiedEveryMocks()
            assertThat(ex).isInstanceOf(AlreadyPromotionSaveException::class.java)
        }

        @Test
        fun `작성자가 프로모션을 수정한다`() {
            // given
            val authorId = randomLong()
            val promotionId = randomLong()
            val originalPromotion = createPromotion(id = promotionId, authorId = authorId)
            val newPromotionType = PromotionType.FREE
            val newTitle = randomString()
            val newContent = randomString()
            val newExternalLink = randomUrl()
            val newStartedAt = randomLocalDate()
            val newEndedAt = randomLocalDate()
            val newPhotographyTypes = setOf(PhotographyType.ID_PHOTO)
            val newImages = listOf(createImage())
            val newActiveRegions = setOf(createRegion())
            val newHashtags = setOf(randomString())
            val updatedPromotion = createPromotion(
                id = promotionId,
                authorId = authorId,
                promotionType = newPromotionType,
                title = newTitle,
                content = newContent,
                externalLink = newExternalLink,
                startedAt = newStartedAt,
                endedAt = newEndedAt,
                viewCount = originalPromotion.viewCount,
                photographyTypes = newPhotographyTypes,
                images = newImages,
                activeRegions = newActiveRegions,
                hashtags = newHashtags,
            )
            every { promotionRepo.getById(id = promotionId) } returns originalPromotion
            every { promotionRepo.update(updatedPromotion) } returns updatedPromotion

            // when
            val result = sut.updatePromotion(
                command = UpdatePromotionUseCase.Command(
                    requestUserId = authorId,
                    promotionId = promotionId,
                    promotionType = newPromotionType,
                    title = newTitle,
                    content = newContent,
                    externalLink = newExternalLink,
                    startedAt = newStartedAt,
                    endedAt = newEndedAt,
                    photographyTypes = newPhotographyTypes,
                    images = newImages,
                    activeRegions = newActiveRegions,
                    hashtags = newHashtags,
                ),
            )

            // then
            verifyOrder {
                promotionRepo.getById(id = promotionId)
                promotionRepo.update(updatedPromotion)
            }
            confirmVerifiedEveryMocks()
            assertThat(result).isEqualTo(updatedPromotion)
        }

        @Test
        fun `작성자가 아닌 사용자가 프로모션을 수정하면, 예외가 발생한다`() {
            // given
            val requestUserId = 1L
            val promotionId = randomLong()
            every {
                promotionRepo.getById(id = promotionId)
            } returns createPromotion(id = promotionId, authorId = 5L)

            // when
            val ex = catchThrowable {
                sut.updatePromotion(
                    command = UpdatePromotionUseCase.Command(
                        requestUserId = requestUserId,
                        promotionId = promotionId,
                        promotionType = PromotionType.FREE,
                        title = randomString(),
                        content = randomString(),
                        externalLink = randomUrl(),
                        startedAt = randomLocalDate(),
                        endedAt = randomLocalDate(),
                        photographyTypes = setOf(PhotographyType.ID_PHOTO),
                        images = listOf(createImage()),
                        activeRegions = setOf(createRegion()),
                        hashtags = setOf(randomString()),
                    ),
                )
            }

            // then
            verify { promotionRepo.getById(id = promotionId) }
            confirmVerifiedEveryMocks()
            assertThat(ex).isInstanceOf(PromotionUpdatePermissionDeniedException::class.java)
        }

        @Test
        fun `작성자가 프로모션을 삭제한다`() {
            // given
            val author = createUser()
            val promotionId = randomLong()
            val promotion = createPromotion(id = promotionId, authorId = author.id)
            every { promotionRepo.getById(promotionId) } returns promotion
            every { promotionRepo.delete(promotion) } just runs

            // when
            sut.deletePromotion(
                DeletePromotionUseCase.Command(requestUser = author, promotionId = promotionId),
            )

            // then
            verifyOrder {
                promotionRepo.getById(promotionId)
                promotionRepo.delete(promotion)
            }
            confirmVerifiedEveryMocks()
        }

        @Test
        fun `관리자가 프로모션을 삭제한다`() {
            // given
            val admin = createUser(id = 1L, roles = setOf(UserRoleType.USER, UserRoleType.ADMIN))
            val promotionId = randomLong()
            val promotion = createPromotion(id = promotionId, authorId = 2L)
            every { promotionRepo.getById(promotionId) } returns promotion
            every { promotionRepo.delete(promotion) } just runs

            // when
            sut.deletePromotion(
                DeletePromotionUseCase.Command(requestUser = admin, promotionId = promotionId),
            )

            // then
            verifyOrder {
                promotionRepo.getById(promotionId)
                promotionRepo.delete(promotion)
            }
            confirmVerifiedEveryMocks()
        }

        @Test
        fun `권한이 없는 유저가 프로모션을 삭제하면, 예외가 발생한다`() {
            // given
            val requestUser = createUser(id = 1L)
            val promotionId = randomLong()
            val promotion = createPromotion(id = promotionId, authorId = 2L)
            every { promotionRepo.getById(promotionId) } returns promotion

            // when
            val ex = catchThrowable {
                sut.deletePromotion(
                    DeletePromotionUseCase.Command(
                        requestUser = requestUser,
                        promotionId = promotionId,
                    ),
                )
            }

            // then
            verify { promotionRepo.getById(promotionId) }
            confirmVerifiedEveryMocks()
            assertThat(ex).isInstanceOf(PromotionDeletePermissionDeniedException::class.java)
        }

        @Test
        fun `프로모션 저장을 해제한다`() {
            // given
            val userId = randomLong()
            val promotionId = randomLong()
            val promotionSave = createPromotionSave(userId = userId, promotionId = promotionId)
            every { promotionSaveRepo.getByUserIdAndPromotionId(userId, promotionId) } returns promotionSave
            every { promotionSaveRepo.delete(promotionSave) } just runs

            // when
            sut.unsavePromotion(UnsavePromotionUseCase.Command(userId, promotionId))

            // then
            verify { promotionSaveRepo.getByUserIdAndPromotionId(userId, promotionId) }
            verify { promotionSaveRepo.delete(promotionSave) }
            confirmVerifiedEveryMocks()
        }

        private fun createPostPromotionCommand() = PostPromotionUseCase.Command(
            authorId = randomLong(),
            promotionType = PromotionType.FREE,
            title = randomString(len = 10),
            content = randomString(),
            externalLink = randomString(),
            startedAt = randomLocalDate(),
            endedAt = randomLocalDate(),
            photographyTypes = setOf(PhotographyType.SNAP),
            images = generateRandomList(maxSize = 10) { createImage() },
            activeRegions = generateRandomSet(maxSize = 5) { createRegion() },
            hashtags = generateRandomSet(maxSize = 5) { randomString() },
        )
    }

    @Nested
    @ActiveProfiles("test")
    @SpringBootTest
    inner class ConcurrencyTest @Autowired constructor(
        private val promotionService: PromotionService,
        private val userRepository: UserCoreRepository,
        private val promotionRepository: PromotionCoreRepository,
    ) {
        @Test
        fun `동시에 여러 번 프로모션 상세 조회할 때, 조회수가 정확히 반영된다`() {
            // given
            val author = userRepository.create(createUser())
            val promotion = promotionRepository.create(createPromotion(authorId = author.id, viewCount = 0))
            val repeatTime = 10

            // when
            val futures = mutableListOf<CompletableFuture<Void>>()
            repeat(repeatTime) {
                futures.add(
                    CompletableFuture.runAsync {
                        promotionService.getPromotionDetail(GetPromotionDetailUseCase.Query(null, promotion.id))
                    },
                )
            }
            CompletableFuture.allOf(*futures.toTypedArray()).join()

            // then
            val viewCount = promotionService.getPromotion(promotion.id).viewCount
            assertThat(viewCount).isEqualTo(repeatTime.toLong())
        }
    }
}
