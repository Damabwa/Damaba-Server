package com.damaba.damaba.application.service.promotion

import com.damaba.damaba.application.port.inbound.promotion.FindPromotionsUseCase
import com.damaba.damaba.application.port.inbound.promotion.PostPromotionUseCase
import com.damaba.damaba.application.port.outbound.promotion.CreatePromotionPort
import com.damaba.damaba.application.port.outbound.promotion.FindPromotionsPort
import com.damaba.damaba.application.port.outbound.promotion.GetPromotionPort
import com.damaba.damaba.application.port.outbound.promotion.UpdatePromotionPort
import com.damaba.damaba.application.port.outbound.user.GetUserPort
import com.damaba.damaba.domain.common.Pagination
import com.damaba.damaba.domain.common.PhotographyType
import com.damaba.damaba.domain.promotion.Promotion
import com.damaba.damaba.domain.promotion.constant.PromotionType
import com.damaba.damaba.util.RandomTestUtils.Companion.generateRandomList
import com.damaba.damaba.util.RandomTestUtils.Companion.generateRandomSet
import com.damaba.damaba.util.RandomTestUtils.Companion.randomInt
import com.damaba.damaba.util.RandomTestUtils.Companion.randomLocalDate
import com.damaba.damaba.util.RandomTestUtils.Companion.randomLong
import com.damaba.damaba.util.RandomTestUtils.Companion.randomString
import com.damaba.damaba.util.fixture.AddressFixture.createAddress
import com.damaba.damaba.util.fixture.FileFixture.createImage
import com.damaba.damaba.util.fixture.PromotionFixture.createPromotion
import com.damaba.damaba.util.fixture.RegionFixture.createRegion
import com.damaba.damaba.util.fixture.UserFixture.createUser
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIterable
import kotlin.test.Test

class PromotionServiceTest {
    private val getPromotionPort: GetPromotionPort = mockk()
    private val getUserPort: GetUserPort = mockk()
    private val findPromotionsPort: FindPromotionsPort = mockk()
    private val createPromotionPort: CreatePromotionPort = mockk()
    private val updatePromotionPort: UpdatePromotionPort = mockk()

    private val sut: PromotionService = PromotionService(
        getPromotionPort,
        getUserPort,
        findPromotionsPort,
        createPromotionPort,
        updatePromotionPort,
    )

    private fun confirmVerifiedEveryMocks() {
        confirmVerified(
            getPromotionPort,
            getUserPort,
            findPromotionsPort,
            createPromotionPort,
            updatePromotionPort,
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
    fun `id가 주어지고, 주어진 id에 해당하는 프로모션의 상세 정보를 조회하면, 조회수가 1 증가하고 프로모션 상세 정보가 반환된다`() {
        // given
        val promotionId = randomLong()
        val originalViewCount = randomLong()
        val promotion = createPromotion(id = promotionId, viewCount = originalViewCount)
        val author = createUser(id = promotion.authorId!!)
        every { getPromotionPort.getById(promotionId) } returns promotion
        every { updatePromotionPort.update(any(Promotion::class)) } returns promotion
        every { getUserPort.getById(promotion.authorId!!) } returns author

        // when
        val result = sut.getPromotionDetail(promotionId)

        // then
        verify { getPromotionPort.getById(promotionId) }
        verify { updatePromotionPort.update(any(Promotion::class)) }
        verify { getUserPort.getById(promotion.authorId!!) }
        confirmVerifiedEveryMocks()
        assertThat(result.id).isEqualTo(promotion.id)
        assertThat(result.viewCount).isEqualTo(originalViewCount + 1)
    }

    @Test
    fun `id가 주어지고, 일치하는 프로모션의 상세 정보를 조회하면, 조회수가 1 증가하고 프로모션 상세 정보가 반환된다, 작성자 정보가 존재하지 않는다면 null로 설정된다`() {
        // given
        val promotionId = randomLong()
        val originalViewCount = randomLong()
        val promotion = createPromotion(id = promotionId, authorId = null, viewCount = originalViewCount)
        every { getPromotionPort.getById(promotionId) } returns promotion
        every { updatePromotionPort.update(any(Promotion::class)) } returns promotion

        // when
        val result = sut.getPromotionDetail(promotionId)

        // then
        verify { getPromotionPort.getById(promotionId) }
        verify { updatePromotionPort.update(any(Promotion::class)) }
        confirmVerifiedEveryMocks()
        assertThat(result.id).isEqualTo(promotion.id)
        assertThat(result.author).isNull()
        assertThat(result.viewCount).isEqualTo(originalViewCount + 1)
    }

    @Test
    fun `프로모션 리스트를 조회한다`() {
        // given
        val query = FindPromotionsUseCase.Query(
            page = 1,
            pageSize = randomInt(min = 5, max = 10),
        )
        val expectedResult = Pagination(
            items = generateRandomList(maxSize = query.pageSize) { createPromotion() },
            page = query.page,
            pageSize = query.pageSize,
            totalPage = 1,
        )
        every {
            findPromotionsPort.findPromotions(page = query.page, pageSize = query.pageSize)
        } returns expectedResult

        // when
        val actualResult = sut.findPromotions(query)

        // then
        verify { findPromotionsPort.findPromotions(page = query.page, pageSize = query.pageSize) }
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
