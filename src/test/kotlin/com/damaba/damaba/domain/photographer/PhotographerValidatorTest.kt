package com.damaba.damaba.domain.photographer

import com.damaba.damaba.domain.common.constant.PhotographyType
import com.damaba.damaba.domain.exception.ValidationException
import com.damaba.damaba.domain.file.Image
import com.damaba.damaba.domain.region.Region
import com.damaba.damaba.util.fixture.FileFixture.createImage
import com.damaba.damaba.util.fixture.RegionFixture.createRegion
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.Test

class PhotographerValidatorTest {
    @ValueSource(strings = ["", "overEighteenCharacters", "withSC!@#$%^"])
    @ParameterizedTest
    fun `유효하지 않은 닉네임이 주어지고, 닉네임 검증을 수행하면, 예외가 발생한다`(invalidNickname: String) {
        assertThrows<ValidationException> { PhotographerValidator.validateNickname(invalidNickname) }
    }

    @MethodSource("invalidPhotographyTypesProvider")
    @ParameterizedTest
    fun `유효하지 않은 촬영 종류 리스트가 주어지고, 사진작가의 촬영 종류 리스트를 검증하면, 예외가 발생한다`(photographyTypes: Set<PhotographyType>) {
        assertThrows<ValidationException> { PhotographerValidator.validateMainPhotographyTypes(photographyTypes) }
    }

    @MethodSource("invalidActiveRegionsProvider")
    @ParameterizedTest
    fun `유효하지 않은 활동 지역 리스트가 주어지고, 사진작가의 활동 지역 리스트를 검증하면, 예외가 발생한다`(activeRegions: Set<Region>) {
        assertThrows<ValidationException> { PhotographerValidator.validateActiveRegions(activeRegions) }
    }

    @Test
    fun `포트폴리오가 비어있으면 예외가 발생한다`() {
        val emptyPortfolio = emptyList<Image>()
        assertThrows<ValidationException> { PhotographerValidator.validatePortfolio(emptyPortfolio) }
    }

    @Test
    fun `포트폴리오가 10장을 초과하면 예외가 발생한다`() {
        val oversizedPortfolio = List(11) { createImage() }
        assertThrows<ValidationException> { PhotographerValidator.validatePortfolio(oversizedPortfolio) }
    }

    @Test
    fun `포트폴리오가 1장에서 10장 사이면 정상적으로 검증된다`() {
        val validPortfolio = List(5) { createImage() }
        PhotographerValidator.validatePortfolio(validPortfolio)
    }

    @Test
    fun `소개가 비어있으면 예외가 발생한다`() {
        assertThrows<ValidationException> { PhotographerValidator.validateDescription("") }
    }

    @Test
    fun `소개가 500자를 초과하면 예외가 발생한다`() {
        val longDescription = "a".repeat(501)
        assertThrows<ValidationException> { PhotographerValidator.validateDescription(longDescription) }
    }

    @Test
    fun `소개가 500자 이하이면 정상적으로 검증된다`() {
        val validDescription = "안녕하세요, 저는 사진작가입니다."
        PhotographerValidator.validateDescription(validDescription)
    }

    companion object {
        @JvmStatic
        private fun invalidPhotographyTypesProvider() = listOf(
            Arguments.of(setOf<PhotographyType>()),
            Arguments.of(
                setOf(
                    PhotographyType.PROFILE,
                    PhotographyType.SELF,
                    PhotographyType.ID_PHOTO,
                    PhotographyType.CONCEPT,
                ),
            ),
        )

        @JvmStatic
        private fun invalidActiveRegionsProvider() = listOf(
            Arguments.of(setOf<Region>()),
            Arguments.of(List(5 + 1) { createRegion() }.toSet()),
        )
    }
}
