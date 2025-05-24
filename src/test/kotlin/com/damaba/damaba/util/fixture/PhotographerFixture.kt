package com.damaba.damaba.util.fixture

import com.damaba.damaba.adapter.outbound.photographer.PhotographerActiveRegionJpaEntity
import com.damaba.damaba.adapter.outbound.photographer.PhotographerAddressJpaEmbeddable
import com.damaba.damaba.adapter.outbound.photographer.PhotographerJpaEntity
import com.damaba.damaba.adapter.outbound.photographer.PhotographerPhotographyTypeJpaEntity
import com.damaba.damaba.adapter.outbound.photographer.PhotographerPortfolioImageJpaEntity
import com.damaba.damaba.domain.common.Address
import com.damaba.damaba.domain.common.constant.PhotographyType
import com.damaba.damaba.domain.file.Image
import com.damaba.damaba.domain.photographer.Photographer
import com.damaba.damaba.domain.photographer.PhotographerListItem
import com.damaba.damaba.domain.photographer.PhotographerSave
import com.damaba.damaba.domain.region.Region
import com.damaba.damaba.domain.user.constant.Gender
import com.damaba.damaba.domain.user.constant.LoginType
import com.damaba.damaba.domain.user.constant.UserRoleType
import com.damaba.damaba.domain.user.constant.UserType
import com.damaba.damaba.util.RandomTestUtils.Companion.randomBoolean
import com.damaba.damaba.util.RandomTestUtils.Companion.randomLong
import com.damaba.damaba.util.RandomTestUtils.Companion.randomString
import com.damaba.damaba.util.RandomTestUtils.Companion.randomUrl
import com.damaba.damaba.util.fixture.AddressFixture.createAddress
import com.damaba.damaba.util.fixture.FileFixture.createImage
import org.springframework.test.util.ReflectionTestUtils

object PhotographerFixture {
    fun createPhotographer(
        id: Long = randomLong(),
        type: UserType = UserType.PHOTOGRAPHER,
        roles: Set<UserRoleType> = setOf(UserRoleType.USER),
        oAuthLoginUid: String = randomString(),
        loginType: LoginType = LoginType.KAKAO,
        nickname: String = randomString(len = 7),
        profileImage: Image? = Image(randomString(), randomUrl()),
        gender: Gender = Gender.MALE,
        instagramId: String? = randomString(len = 30),
        mainPhotographyTypes: Set<PhotographyType> = setOf(PhotographyType.PROFILE),
        contactLink: String? = randomString(),
        description: String? = randomString(),
        address: Address? = createAddress(),
        portfolio: List<Image> = emptyList(),
        activeRegions: Set<Region> = emptySet(),
    ) = Photographer(
        id = id,
        type = type,
        roles = roles,
        loginType = loginType,
        oAuthLoginUid = oAuthLoginUid,
        nickname = nickname,
        profileImage = profileImage,
        gender = gender,
        instagramId = instagramId,
        mainPhotographyTypes = mainPhotographyTypes,
        contactLink = contactLink,
        description = description,
        address = address,
        portfolio = portfolio,
        activeRegions = activeRegions,
    )

    fun createPhotographerListItem(
        id: Long = randomLong(),
        nickname: String = randomString(len = 7),
        profileImage: Image? = createImage(),
        mainPhotographyTypes: Set<PhotographyType> = setOf(PhotographyType.PROFILE),
        isSaved: Boolean = randomBoolean(),
    ) = PhotographerListItem(
        id = id,
        nickname = nickname,
        profileImage = profileImage,
        isSaved = isSaved,
        mainPhotographyTypes = mainPhotographyTypes,
    )

    fun createPhotographerSave(
        id: Long = randomLong(),
        userId: Long = randomLong(),
        photographerId: Long = randomLong(),
    ) = PhotographerSave(
        id = id,
        userId = userId,
        photographerId = photographerId,
    )

    fun createPhotographerJpaEntity(
        id: Long = randomLong(),
        contactLink: String? = randomString(),
        description: String? = randomString(),
        address: PhotographerAddressJpaEmbeddable? = null,
        mainPhotographyTypes: Set<PhotographerPhotographyTypeJpaEntity> = emptySet(),
        portfolio: List<PhotographerPortfolioImageJpaEntity> = emptyList(),
        activeRegions: Set<PhotographerActiveRegionJpaEntity> = emptySet(),
    ): PhotographerJpaEntity {
        val photographerJpaEntity = PhotographerJpaEntity(
            userId = id,
            contactLink = contactLink,
            description = description,
            address = address,
        )
        photographerJpaEntity.mainPhotographyTypes.addAll(mainPhotographyTypes)
        ReflectionTestUtils.setField(photographerJpaEntity, "_portfolio", portfolio)
        photographerJpaEntity.activeRegions.addAll(activeRegions)
        return photographerJpaEntity
    }
}
