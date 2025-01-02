package com.damaba.damaba.application.service.promotion

import com.damaba.damaba.application.port.inbound.promotion.FindPromotionsUseCase
import com.damaba.damaba.application.port.inbound.promotion.PostPromotionUseCase
import com.damaba.damaba.application.port.outbound.promotion.FindPromotionsPort
import com.damaba.damaba.application.port.outbound.promotion.GetPromotionPort
import com.damaba.damaba.application.port.outbound.promotion.SavePromotionPort
import com.damaba.damaba.domain.common.Pagination
import com.damaba.damaba.domain.promotion.Promotion
import com.damaba.damaba.domain.promotion.constant.EventType
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
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIterable
import kotlin.test.Test

class PromotionServiceTest {
    private val getPromotionPort: GetPromotionPort = mockk()
    private val findPromotionsPort: FindPromotionsPort = mockk()
    private val savePromotionPort: SavePromotionPort = mockk()

    private val sut: PromotionService = PromotionService(
        getPromotionPort,
        findPromotionsPort,
        savePromotionPort,
    )

    private fun confirmVerifiedEveryMocks() {
        confirmVerified(getPromotionPort, findPromotionsPort, savePromotionPort)
    }

    @Test
    fun `프로모션 id가 주어지고, 일치하는 프로모션을 상세조회한다`() {
        // given
        val promotionId = randomLong()
        val expectedResult = createPromotion()
        every { getPromotionPort.getById(promotionId) } returns expectedResult

        // when
        val actualResult = sut.getPromotionDetail(promotionId)

        // then
        verify { getPromotionPort.getById(promotionId) }
        confirmVerifiedEveryMocks()
        assertThat(actualResult).isEqualTo(expectedResult)
    }

    @Test
    fun `프로모션 리스트를 조회한다`() {
        // given
        val query = FindPromotionsUseCase.Query(
            page = randomInt(max = 10),
            pageSize = randomInt(max = 10),
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
        every { savePromotionPort.save(any(Promotion::class)) } returns expectedResult

        // when
        val actualResult = sut.postPromotion(command)

        // then
        verify { savePromotionPort.save(any(Promotion::class)) }
        confirmVerifiedEveryMocks()
        assertThat(actualResult).isEqualTo(expectedResult)
        assertThatIterable(actualResult.images).isEqualTo(expectedResult.images)
        assertThatIterable(actualResult.activeRegions).isEqualTo(expectedResult.activeRegions)
        assertThatIterable(actualResult.hashtags).isEqualTo(expectedResult.hashtags)
    }

    private fun createPostPromotionCommand() = PostPromotionUseCase.Command(
        authorId = randomLong(),
        type = PromotionType.EVENT,
        eventType = EventType.FREE,
        title = randomString(len = 10),
        content = randomString(),
        address = createAddress(),
        externalLink = randomString(),
        startedAt = randomLocalDate(),
        endedAt = randomLocalDate(),
        photographerName = randomString(),
        photographerInstagramId = randomString(),
        images = generateRandomList(maxSize = 10) { createImage() },
        activeRegions = generateRandomSet(maxSize = 5) { createRegion() },
        hashtags = generateRandomSet(maxSize = 5) { randomString() },
    )
}
