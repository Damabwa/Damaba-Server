package com.damaba.damaba.domain.photographer

import com.damaba.common_exception.ValidationException
import com.damaba.damaba.domain.common.PhotographyType
import com.damaba.damaba.domain.region.Region
import com.damaba.damaba.util.fixture.RegionFixture.createRegion
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource

class PhotographerValidatorTest {
    @ValueSource(strings = ["", "overFifteenCharacters", "withSC!@#$%^"])
    @ParameterizedTest
    fun `유효하지 않은 닉네임이 주어지고, 닉네임 검증을 수행하면, 예외가 발생한다`(
        invalidNickname: String,
    ) {
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
            Arguments.of(List(4) { createRegion() }.toSet()),
        )
    }
}
