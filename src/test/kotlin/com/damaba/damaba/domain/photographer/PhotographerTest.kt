package com.damaba.damaba.domain.photographer

import com.damaba.damaba.domain.common.constant.PhotographyType
import com.damaba.damaba.domain.user.constant.Gender
import com.damaba.damaba.domain.user.constant.UserType
import com.damaba.damaba.util.RandomTestUtils.Companion.generateRandomSet
import com.damaba.damaba.util.RandomTestUtils.Companion.randomString
import com.damaba.damaba.util.fixture.FileFixture.createImage
import com.damaba.damaba.util.fixture.PhotographerFixture.createPhotographer
import com.damaba.damaba.util.fixture.RegionFixture.createRegion
import org.assertj.core.api.Assertions.assertThat
import kotlin.test.Test

class PhotographerTest {
    @Test
    fun `등록되지 않은 사진작가를 사진작가로 등록한다`() {
        // given
        val nickname = randomString(len = 10)
        val gender = Gender.FEMALE
        val instagramId = randomString(len = 15)
        val profileImage = createImage()
        val mainPhotographyTypes = setOf(PhotographyType.SELF, PhotographyType.PROFILE)
        val activeRegions = generateRandomSet(maxSize = 3) { createRegion() }
        val photographer = createPhotographer(type = UserType.UNDEFINED)

        // when
        photographer.registerPhotographer(
            nickname,
            gender,
            instagramId,
            profileImage,
            mainPhotographyTypes,
            activeRegions,
        )

        // then
        assertThat(photographer.nickname).isEqualTo(nickname)
        assertThat(photographer.gender).isEqualTo(gender)
        assertThat(photographer.instagramId).isEqualTo(instagramId)
        assertThat(photographer.profileImage).isEqualTo(profileImage)
        assertThat(photographer.mainPhotographyTypes).isEqualTo(mainPhotographyTypes)
        assertThat(photographer.activeRegions).isEqualTo(activeRegions)
    }

    @Test
    fun `id가 동일한 photographer는 같은 객체이다`() {
        // given
        val photographer1 = createPhotographer(id = 1L)
        val photographer2 = createPhotographer(id = 1L)

        // when
        val result = photographer1 == photographer2

        // then
        assertThat(result).isTrue()
    }

    @Test
    fun `id가 다른 photographer는 다른 객체이다`() {
        // given
        val photographer1 = createPhotographer(id = 1L)
        val photographer2 = createPhotographer(id = 2L)

        // when
        val result = photographer1 == photographer2

        // then
        assertThat(result).isFalse()
    }
}
