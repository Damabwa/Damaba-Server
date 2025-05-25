package com.damaba.damaba.application.promotion.dto

import com.damaba.damaba.domain.common.constant.PhotographyType
import com.damaba.damaba.domain.exception.ValidationException
import com.damaba.damaba.domain.file.File
import com.damaba.damaba.domain.promotion.constant.PromotionType
import com.damaba.damaba.domain.region.Region
import com.damaba.damaba.util.RandomTestUtils.Companion.randomLong
import com.damaba.damaba.util.RandomTestUtils.Companion.randomString
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
            title: String = "Valid title",
            content: String = "Valid content",
            promotionType: PromotionType = PromotionType.FREE,
            photographyTypes: Set<PhotographyType> = setOf(PhotographyType.SNAP),
            images: List<File> = List(3) { createImage() },
            activeRegions: Set<Region> = setOf(Region("서울", "강남구")),
            hashtags: Set<String> = setOf("tag1", "tag2"),
        ) = PostPromotionCommand(
            authorId = randomLong(),
            promotionType = promotionType,
            title = title,
            content = content,
            externalLink = "https://example.com",
            startedAt = LocalDate.now(),
            endedAt = LocalDate.now().plusDays(1),
            photographyTypes = photographyTypes,
            images = images,
            activeRegions = activeRegions,
            hashtags = hashtags,
        )
    }
}
