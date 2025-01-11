package com.damaba.damaba.application.port.inbound.promotion

import com.damaba.damaba.domain.common.Address
import com.damaba.damaba.domain.common.PhotographyType
import com.damaba.damaba.domain.exception.ValidationException
import com.damaba.damaba.domain.file.File
import com.damaba.damaba.domain.promotion.constant.PromotionType
import com.damaba.damaba.domain.region.Region
import com.damaba.damaba.util.RandomTestUtils.Companion.randomLong
import com.damaba.damaba.util.RandomTestUtils.Companion.randomString
import com.damaba.damaba.util.fixture.AddressFixture.createAddress
import com.damaba.damaba.util.fixture.FileFixture.createImage
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.time.LocalDate
import kotlin.test.Test

class PostPromotionUseCaseCommandTest {
    @ParameterizedTest
    @MethodSource("invalidAddressProvider")
    fun `유효하지 않은 주소가 입력되면 ValidationException이 발생한다`(address: Address) {
        val exception = catchThrowable { createCommand(address = address) }
        assertThat(exception).isInstanceOf(ValidationException::class.java)
    }

    @ParameterizedTest
    @MethodSource("invalidTitleProvider")
    fun `유효하지 않은 제목이 입력되면 ValidationException이 발생한다`(title: String) {
        val exception = catchThrowable { createCommand(title = title) }
        assertThat(exception).isInstanceOf(ValidationException::class.java)
    }

    @Test
    fun `500 글자를 초과한 내용이 입력되면 ValidationException이 발생한다`() {
        val exception = catchThrowable { createCommand(content = randomString(len = 1000)) }
        assertThat(exception).isInstanceOf(ValidationException::class.java)
    }

    @ParameterizedTest
    @MethodSource("invalidImageSizeProvider")
    fun `이미지 개수가 유효하지 않으면 ValidationException이 발생한다`(images: List<File>) {
        val exception = catchThrowable { createCommand(images = images) }
        assertThat(exception).isInstanceOf(ValidationException::class.java)
    }

    @Test
    fun `활동 지역이 없으면 ValidationException이 발생한다`() {
        val exception = catchThrowable { createCommand(activeRegions = emptySet()) }
        assertThat(exception).isInstanceOf(ValidationException::class.java)
    }

    companion object {
        @JvmStatic
        fun invalidAddressProvider() = listOf(
            Arguments.of(Address(" ", "강남구", "도로명 주소", "지번 주소")),
            Arguments.of(Address("서울", " ", "도로명 주소", "지번 주소")),
            Arguments.of(Address("서울", "강남구", " ", "지번 주소")),
            Arguments.of(Address("서울", "강남구", "도로명 주소", " ")),
        )

        @JvmStatic
        fun invalidTitleProvider() = listOf(
            Arguments.of(" "),
            Arguments.of("A"),
            Arguments.of(randomString(len = 30)),
        )

        @JvmStatic
        fun invalidImageSizeProvider() = listOf(
            Arguments.of(emptyList<File>()),
            Arguments.of(List(11) { createImage() }),
        )

        private fun createCommand(
            address: Address = createAddress(),
            title: String = "Valid title",
            content: String = "Valid content",
            promotionType: PromotionType = PromotionType.FREE,
            photographyTypes: Set<PhotographyType> = setOf(PhotographyType.SNAP),
            images: List<File> = List(3) { createImage() },
            activeRegions: Set<Region> = setOf(Region("서울", "강남구")),
            hashtags: Set<String> = setOf("tag1", "tag2"),
        ) = PostPromotionUseCase.Command(
            authorId = randomLong(),
            promotionType = promotionType,
            title = title,
            content = content,
            address = address,
            externalLink = "http://example.com",
            startedAt = LocalDate.now(),
            endedAt = LocalDate.now().plusDays(1),
            photographerName = "Photographer",
            photographerInstagramId = "insta",
            photographyTypes = photographyTypes,
            images = images,
            activeRegions = activeRegions,
            hashtags = hashtags,
        )
    }
}
