package com.damaba.damaba.application.service.promotion

import com.damaba.common_file.application.port.outbound.UploadFilesPort
import com.damaba.common_file.domain.FileUploadRollbackEvent
import com.damaba.damaba.application.port.inbound.promotion.PostPromotionUseCase
import com.damaba.damaba.application.port.outbound.GetPromotionPort
import com.damaba.damaba.application.port.outbound.common.PublishEventPort
import com.damaba.damaba.application.port.outbound.promotion.SavePromotionPort
import com.damaba.damaba.domain.promotion.Promotion
import com.damaba.damaba.domain.promotion.constant.EventType
import com.damaba.damaba.domain.promotion.constant.PromotionType
import com.damaba.damaba.util.RandomTestUtils.Companion.generateRandomList
import com.damaba.damaba.util.RandomTestUtils.Companion.generateRandomSet
import com.damaba.damaba.util.RandomTestUtils.Companion.randomLocalDate
import com.damaba.damaba.util.RandomTestUtils.Companion.randomLong
import com.damaba.damaba.util.RandomTestUtils.Companion.randomString
import com.damaba.damaba.util.TestFixture.createAddress
import com.damaba.damaba.util.TestFixture.createPromotion
import com.damaba.damaba.util.TestFixture.createRegion
import com.damaba.damaba.util.TestFixture.createUploadFile
import com.damaba.damaba.util.TestFixture.createUploadedFile
import io.mockk.Runs
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIterable
import org.assertj.core.api.Assertions.catchThrowable
import kotlin.test.Test

class PromotionServiceTest {
    private val getPromotionPort: GetPromotionPort = mockk()
    private val savePromotionPort: SavePromotionPort = mockk()
    private val uploadFilesPort: UploadFilesPort = mockk()
    private val publishEventPort: PublishEventPort = mockk()

    private val sut: PromotionService = PromotionService(
        getPromotionPort,
        savePromotionPort,
        uploadFilesPort,
        publishEventPort,
    )

    private fun confirmVerifiedEveryMocks() {
        confirmVerified(getPromotionPort, savePromotionPort, uploadFilesPort, publishEventPort)
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
    fun `등록할 이벤트 프로모션 정보들이 주어지고, 주어진 정보로 이벤트 프로모션을 생성 및 등록한다`() {
        // given
        val command = createPostPromotionCommand(PromotionType.EVENT, EventType.FREE)
        val expectedResult = createPromotion()
        every {
            uploadFilesPort.upload(command.images, any(String::class))
        } returns List(3) { createUploadedFile() }
        every { savePromotionPort.save(any(Promotion::class)) } returns expectedResult

        // when
        val actualResult = sut.postPromotion(command)

        // then
        verifyOrder {
            uploadFilesPort.upload(command.images, any(String::class))
            savePromotionPort.save(any(Promotion::class))
        }
        confirmVerifiedEveryMocks()
        assertThat(actualResult).isEqualTo(expectedResult)
        assertThatIterable(actualResult.images).isEqualTo(expectedResult.images)
        assertThatIterable(actualResult.activeRegions).isEqualTo(expectedResult.activeRegions)
        assertThatIterable(actualResult.hashtags).isEqualTo(expectedResult.hashtags)
    }

    @Test
    fun `등록할 모델 프로모션 정보들이 주어지고, 주어진 정보로 모델 프로모션을 생성 및 등록한다`() {
        // given
        val command = createPostPromotionCommand(PromotionType.MODEL, null)
        val expectedResult = createPromotion()
        every {
            uploadFilesPort.upload(command.images, any(String::class))
        } returns List(3) { createUploadedFile() }
        every { savePromotionPort.save(any(Promotion::class)) } returns expectedResult

        // when
        val actualResult = sut.postPromotion(command)

        // then
        verifyOrder {
            uploadFilesPort.upload(command.images, any(String::class))
            savePromotionPort.save(any(Promotion::class))
        }
        confirmVerifiedEveryMocks()
        assertThat(actualResult).isEqualTo(expectedResult)
        assertThatIterable(actualResult.images).isEqualTo(expectedResult.images)
        assertThatIterable(actualResult.activeRegions).isEqualTo(expectedResult.activeRegions)
        assertThatIterable(actualResult.hashtags).isEqualTo(expectedResult.hashtags)
    }

    @Test
    fun `프로모션을 생성 및 등록한다, 이미지는 성공적으로 업로드 되었지만 프로모션 저장에 실패할 경우, 파일 롤백 이벤트를 발행하고 예외가 발생한다`() {
        // given
        val command = createPostPromotionCommand(PromotionType.EVENT, EventType.FREE)
        val expectedThrownException = IllegalStateException()
        every {
            uploadFilesPort.upload(command.images, any(String::class))
        } returns List(3) { createUploadedFile() }
        every { savePromotionPort.save(any(Promotion::class)) } throws expectedThrownException
        every { publishEventPort.publish(any(FileUploadRollbackEvent::class)) } just Runs

        // when
        val actualThrownException = catchThrowable { sut.postPromotion(command) }

        // then
        verifyOrder {
            uploadFilesPort.upload(command.images, any(String::class))
            savePromotionPort.save(any(Promotion::class))
            publishEventPort.publish(any(FileUploadRollbackEvent::class))
        }
        confirmVerifiedEveryMocks()
        assertThat(actualThrownException).isInstanceOf(expectedThrownException::class.java)
    }

    private fun createPostPromotionCommand(
        promotionType: PromotionType,
        eventType: EventType?,
    ) = PostPromotionUseCase.Command(
        authorId = randomLong(),
        type = promotionType,
        eventType = eventType,
        title = randomString(len = 10),
        content = randomString(),
        address = createAddress(),
        externalLink = randomString(),
        startedAt = randomLocalDate(),
        endedAt = randomLocalDate(),
        photographerName = randomString(),
        photographerInstagramId = randomString(),
        images = generateRandomList(maxSize = 10) { createUploadFile() },
        activeRegions = generateRandomSet(maxSize = 5) { createRegion() },
        hashtags = generateRandomSet(maxSize = 5) { randomString() },
    )
}
