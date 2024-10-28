package com.damaba.damaba.application.port.inbound.promotion

import com.damaba.common_exception.ValidationException
import com.damaba.common_file.domain.UploadFile
import com.damaba.damaba.domain.common.Address
import com.damaba.damaba.domain.promotion.constant.EventType
import com.damaba.damaba.domain.promotion.constant.PromotionType
import com.damaba.damaba.domain.region.Region
import com.damaba.damaba.util.RandomTestUtils.Companion.randomLong
import com.damaba.damaba.util.RandomTestUtils.Companion.randomString
import com.damaba.damaba.util.TestFixture.createAddress
import com.damaba.damaba.util.TestFixture.createUploadFile
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.time.LocalDate
import kotlin.test.Test

class PostPromotionUseCaseTest {
    @Nested
    inner class CommandTest {
        @ParameterizedTest
        @MethodSource("com.damaba.damaba.application.port.inbound.promotion.PostPromotionUseCaseTest#validCommandProvider")
        fun `valid command creation test`(command: PostPromotionUseCase.Command) {
        }

        @ParameterizedTest
        @MethodSource("com.damaba.damaba.application.port.inbound.promotion.PostPromotionUseCaseTest#invalidAddressProvider")
        fun `유효하지 않은 주소가 입력되면 ValidationException이 발생한다`(address: Address) {
            val exception = catchThrowable { createCommand(address = address) }
            assertThat(exception).isInstanceOf(ValidationException::class.java)
        }

        @ParameterizedTest
        @MethodSource("com.damaba.damaba.application.port.inbound.promotion.PostPromotionUseCaseTest#invalidTitleProvider")
        fun `유효하지 않은 제목이 입력되면 ValidationException이 발생한다`(title: String) {
            val exception = catchThrowable { createCommand(title = title) }
            assertThat(exception).isInstanceOf(ValidationException::class.java)
        }

        @Test
        fun `500 글자를 초과한 내용이 입력되면 ValidationException이 발생한다`() {
            val exception = catchThrowable { createCommand(content = randomString(len = 1000)) }
            assertThat(exception).isInstanceOf(ValidationException::class.java)
        }

        @Test
        fun `프로모션 유형이 이벤트이나 이벤트 유형이 없으면 ValidationException이 발생한다`() {
            val exception = catchThrowable { createCommand(promotionType = PromotionType.EVENT, eventType = null) }
            assertThat(exception).isInstanceOf(ValidationException::class.java)
        }

        @ParameterizedTest
        @MethodSource("com.damaba.damaba.application.port.inbound.promotion.PostPromotionUseCaseTest#invalidImageSizeProvider")
        fun `이미지 개수가 유효하지 않으면 ValidationException이 발생한다`(images: List<UploadFile>) {
            val exception = catchThrowable { createCommand(images = images) }
            assertThat(exception).isInstanceOf(ValidationException::class.java)
        }

        @Test
        fun `활동 지역이 없으면 ValidationException이 발생한다`() {
            val exception = catchThrowable { createCommand(activeRegions = emptySet()) }
            assertThat(exception).isInstanceOf(ValidationException::class.java)
        }
    }

    companion object {
        @JvmStatic
        fun validCommandProvider() = listOf(
            Arguments.of(createCommand(address = Address("서울", "강남구", "도로명 주소", " "))),
            Arguments.of(createCommand(address = Address("서울", "강남구", " ", "지번 주소"))),
        )

        @JvmStatic
        fun invalidAddressProvider() = listOf(
            Arguments.of(Address(" ", "강남구", "도로명 주소", "지번 주소")),
            Arguments.of(Address("서울", " ", "도로명 주소", "지번 주소")),
            Arguments.of(Address("서울", "강남구", null, null)),
            Arguments.of(Address("서울", "강남구", " ", null)),
            Arguments.of(Address("서울", "강남구", null, " ")),
        )

        @JvmStatic
        fun invalidTitleProvider() = listOf(
            Arguments.of(" "),
            Arguments.of("A"),
            Arguments.of(randomString(len = 30)),
        )

        @JvmStatic
        fun invalidImageSizeProvider() = listOf(
            Arguments.of(emptyList<UploadFile>()),
            Arguments.of(List(11) { createUploadFile() }),
        )

        private fun createCommand(
            address: Address = createAddress(),
            title: String = "Valid title",
            content: String = "Valid content",
            promotionType: PromotionType = PromotionType.EVENT,
            eventType: EventType? = EventType.FREE,
            images: List<UploadFile> = List(3) { createUploadFile() },
            activeRegions: Set<Region> = setOf(Region("서울", "강남구")),
        ) = PostPromotionUseCase.Command(
            authorId = randomLong(),
            type = promotionType,
            eventType = eventType,
            title = title,
            content = content,
            address = address,
            externalLink = "http://example.com",
            startedAt = LocalDate.now(),
            endedAt = LocalDate.now().plusDays(1),
            photographerName = "Photographer",
            photographerInstagramId = "insta",
            images = images,
            activeRegions = activeRegions,
            hashtags = setOf("tag1", "tag2"),
        )
    }
}
