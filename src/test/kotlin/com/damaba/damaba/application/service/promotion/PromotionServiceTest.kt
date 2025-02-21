package com.damaba.damaba.application.service.promotion

import com.damaba.damaba.adapter.outbound.promotion.PromotionCoreRepository
import com.damaba.damaba.adapter.outbound.user.UserCoreRepository
import com.damaba.damaba.application.port.inbound.promotion.FindPromotionListUseCase
import com.damaba.damaba.application.port.inbound.promotion.GetPromotionDetailUseCase
import com.damaba.damaba.application.port.inbound.promotion.PostPromotionUseCase
import com.damaba.damaba.application.port.inbound.promotion.SavePromotionUseCase
import com.damaba.damaba.application.port.inbound.promotion.UnsavePromotionUseCase
import com.damaba.damaba.application.port.outbound.promotion.CountSavedPromotionPort
import com.damaba.damaba.application.port.outbound.promotion.CreatePromotionPort
import com.damaba.damaba.application.port.outbound.promotion.CreateSavedPromotionPort
import com.damaba.damaba.application.port.outbound.promotion.DeleteSavedPromotionPort
import com.damaba.damaba.application.port.outbound.promotion.ExistsSavedPromotionPort
import com.damaba.damaba.application.port.outbound.promotion.FindPromotionListPort
import com.damaba.damaba.application.port.outbound.promotion.GetPromotionPort
import com.damaba.damaba.application.port.outbound.promotion.GetSavedPromotionPort
import com.damaba.damaba.application.port.outbound.promotion.UpdatePromotionPort
import com.damaba.damaba.application.port.outbound.user.GetUserPort
import com.damaba.damaba.domain.common.Pagination
import com.damaba.damaba.domain.common.PhotographyType
import com.damaba.damaba.domain.promotion.Promotion
import com.damaba.damaba.domain.promotion.SavedPromotion
import com.damaba.damaba.domain.promotion.constant.PromotionProgressStatus
import com.damaba.damaba.domain.promotion.constant.PromotionSortType
import com.damaba.damaba.domain.promotion.constant.PromotionType
import com.damaba.damaba.domain.promotion.exception.AlreadySavedPromotionException
import com.damaba.damaba.domain.region.RegionFilterCondition
import com.damaba.damaba.util.RandomTestUtils.Companion.generateRandomList
import com.damaba.damaba.util.RandomTestUtils.Companion.generateRandomSet
import com.damaba.damaba.util.RandomTestUtils.Companion.randomBoolean
import com.damaba.damaba.util.RandomTestUtils.Companion.randomInt
import com.damaba.damaba.util.RandomTestUtils.Companion.randomLocalDate
import com.damaba.damaba.util.RandomTestUtils.Companion.randomLong
import com.damaba.damaba.util.RandomTestUtils.Companion.randomString
import com.damaba.damaba.util.fixture.AddressFixture.createAddress
import com.damaba.damaba.util.fixture.FileFixture.createImage
import com.damaba.damaba.util.fixture.PromotionFixture.createPromotion
import com.damaba.damaba.util.fixture.PromotionFixture.createPromotionListItem
import com.damaba.damaba.util.fixture.PromotionFixture.createSavedPromotion
import com.damaba.damaba.util.fixture.RegionFixture.createRegion
import com.damaba.damaba.util.fixture.UserFixture.createUser
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
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
        private val getUserPort: GetUserPort = mockk()
        private val getPromotionPort: GetPromotionPort = mockk()
        private val findPromotionListPort: FindPromotionListPort = mockk()
        private val updatePromotionPort: UpdatePromotionPort = mockk()
        private val createSavedPromotionPort: CreateSavedPromotionPort = mockk()
        private val getSavedPromotionPort: GetSavedPromotionPort = mockk()
        private val existsSavedPromotionPort: ExistsSavedPromotionPort = mockk()
        private val countSavedPromotionPort: CountSavedPromotionPort = mockk()
        private val createPromotionPort: CreatePromotionPort = mockk()
        private val deleteSavedPromotionPort: DeleteSavedPromotionPort = mockk()

        private val sut: PromotionService = PromotionService(
            getUserPort,
            getPromotionPort,
            findPromotionListPort,
            createPromotionPort,
            updatePromotionPort,
            getSavedPromotionPort,
            existsSavedPromotionPort,
            countSavedPromotionPort,
            createSavedPromotionPort,
            deleteSavedPromotionPort,
        )

        private fun confirmVerifiedEveryMocks() {
            confirmVerified(
                getUserPort,
                getPromotionPort,
                findPromotionListPort,
                createPromotionPort,
                updatePromotionPort,
                getSavedPromotionPort,
                existsSavedPromotionPort,
                countSavedPromotionPort,
                createSavedPromotionPort,
                deleteSavedPromotionPort,
            )
        }

        @Test
        fun `프로모션 id가 주어지고, 일치하는 프로모션을 단건 조회한다`() {
            // given
            val promotionId = randomLong()
            val expectedResult = createPromotion()
            every { getPromotionPort.getById(promotionId) } returns expectedResult

            // when
            val actualResult = sut.getPromotion(promotionId)

            // then
            verify { getPromotionPort.getById(promotionId) }
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
            every { getPromotionPort.getById(promotionId) } returns promotion
            every { updatePromotionPort.update(any(Promotion::class)) } returns promotion
            every { getUserPort.getById(promotion.authorId!!) } returns author
            every { countSavedPromotionPort.countByPromotionId(promotionId) } returns expectedSaveCount
            every {
                existsSavedPromotionPort.existsByUserIdAndPromotionId(requestUserId, promotionId)
            } returns expectedIsSaved

            // when
            val result = sut.getPromotionDetail(GetPromotionDetailUseCase.Query(requestUserId, promotionId))

            // then
            verify { getPromotionPort.getById(promotionId) }
            verify { updatePromotionPort.update(any(Promotion::class)) }
            verify { getUserPort.getById(promotion.authorId!!) }
            verify { countSavedPromotionPort.countByPromotionId(promotionId) }
            verify { existsSavedPromotionPort.existsByUserIdAndPromotionId(requestUserId, promotionId) }
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
            every { getPromotionPort.getById(promotionId) } returns promotion
            every { updatePromotionPort.update(any(Promotion::class)) } returns promotion
            every { getUserPort.getById(promotion.authorId!!) } returns author
            every { countSavedPromotionPort.countByPromotionId(promotionId) } returns expectedSaveCount

            // when
            val result = sut.getPromotionDetail(GetPromotionDetailUseCase.Query(requestUserId, promotionId))

            // then
            verify { getPromotionPort.getById(promotionId) }
            verify { updatePromotionPort.update(any(Promotion::class)) }
            verify { getUserPort.getById(promotion.authorId!!) }
            verify { countSavedPromotionPort.countByPromotionId(promotionId) }
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
            every { getPromotionPort.getById(promotionId) } returns promotion
            every { updatePromotionPort.update(any(Promotion::class)) } returns promotion
            every { countSavedPromotionPort.countByPromotionId(promotionId) } returns expectedSaveCount
            every {
                existsSavedPromotionPort.existsByUserIdAndPromotionId(requestUserId, promotionId)
            } returns expectedIsSaved

            // when
            val result = sut.getPromotionDetail(GetPromotionDetailUseCase.Query(requestUserId, promotionId))

            // then
            verify { getPromotionPort.getById(promotionId) }
            verify { updatePromotionPort.update(any(Promotion::class)) }
            verify { countSavedPromotionPort.countByPromotionId(promotionId) }
            verify { existsSavedPromotionPort.existsByUserIdAndPromotionId(requestUserId, promotionId) }
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
                findPromotionListPort.findPromotionList(
                    reqUserId = query.reqUserId,
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
                findPromotionListPort.findPromotionList(
                    reqUserId = query.reqUserId,
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
            every { createPromotionPort.create(any(Promotion::class)) } returns expectedResult

            // when
            val actualResult = sut.postPromotion(command)

            // then
            verify { createPromotionPort.create(any(Promotion::class)) }
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
            every { existsSavedPromotionPort.existsByUserIdAndPromotionId(userId, promotionId) } returns false
            every { createSavedPromotionPort.create(any(SavedPromotion::class)) } just runs

            // when
            sut.savePromotion(SavePromotionUseCase.Command(userId, promotionId))

            // then
            verify { existsSavedPromotionPort.existsByUserIdAndPromotionId(userId, promotionId) }
            verify { createSavedPromotionPort.create(any(SavedPromotion::class)) }
            confirmVerifiedEveryMocks()
        }

        @Test
        fun `프로모션을 저장한다, 이미 저장된 프로모션이라면 예외가 발생한다`() {
            // given
            val userId = randomLong()
            val promotionId = randomLong()
            every { existsSavedPromotionPort.existsByUserIdAndPromotionId(userId, promotionId) } returns true

            // when
            val ex = catchThrowable { sut.savePromotion(SavePromotionUseCase.Command(userId, promotionId)) }

            // then
            verify { existsSavedPromotionPort.existsByUserIdAndPromotionId(userId, promotionId) }
            confirmVerifiedEveryMocks()
            assertThat(ex).isInstanceOf(AlreadySavedPromotionException::class.java)
        }

        @Test
        fun `프로모션 저장을 해제한다`() {
            // given
            val userId = randomLong()
            val promotionId = randomLong()
            val savedPromotion = createSavedPromotion(userId = userId, promotionId = promotionId)
            every { getSavedPromotionPort.getByUserIdAndPromotionId(userId, promotionId) } returns savedPromotion
            every { deleteSavedPromotionPort.delete(savedPromotion) } just runs

            // when
            sut.unsavePromotion(UnsavePromotionUseCase.Command(userId, promotionId))

            // then
            verify { getSavedPromotionPort.getByUserIdAndPromotionId(userId, promotionId) }
            verify { deleteSavedPromotionPort.delete(savedPromotion) }
            confirmVerifiedEveryMocks()
        }

        private fun createPostPromotionCommand() = PostPromotionUseCase.Command(
            authorId = randomLong(),
            promotionType = PromotionType.FREE,
            title = randomString(len = 10),
            content = randomString(),
            address = createAddress(),
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
