package com.damaba.damaba.domain.photographer

import com.damaba.damaba.domain.common.constant.PhotographyType
import com.damaba.damaba.util.fixture.FileFixture.createImage
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class PhotographerListItemTest {
    @ParameterizedTest
    @MethodSource("photographerListItemProvider")
    fun `PhotographerListItem 객체 생성 테스트`(
        photographerListItem: PhotographerListItem,
        isProfileImageNull: Boolean,
    ) {
        assertThat(1L).isEqualTo(photographerListItem.id)
        assertThat("JohnDoe").isEqualTo(photographerListItem.nickname)
        assertThat(photographerListItem.profileImage == null).isEqualTo(isProfileImageNull)
        assertThat(photographerListItem.isSaved).isTrue()
        assertThat(setOf(PhotographyType.SNAP)).isEqualTo(photographerListItem.mainPhotographyTypes)
    }

    companion object {
        @JvmStatic
        private fun photographerListItemProvider() = listOf(
            Arguments.of(
                PhotographerListItem(
                    id = 1L,
                    nickname = "JohnDoe",
                    profileImage = createImage(),
                    isSaved = true,
                    mainPhotographyTypes = setOf(PhotographyType.SNAP),
                ),
                false,
            ),
            Arguments.of(
                PhotographerListItem(
                    id = 1L,
                    nickname = "JohnDoe",
                    profileImage = null,
                    isSaved = true,
                    mainPhotographyTypes = setOf(PhotographyType.SNAP),
                ),
                true,
            ),
        )
    }
}
