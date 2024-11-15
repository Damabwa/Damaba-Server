package com.damaba.damaba.util

import com.damaba.common_file.domain.File
import com.damaba.common_file.domain.Image
import com.damaba.damaba.adapter.inbound.common.dto.ImageRequest
import com.damaba.damaba.adapter.inbound.region.dto.RegionRequest
import com.damaba.damaba.adapter.outbound.photographer.BusinessScheduleJpaEmbeddable
import com.damaba.damaba.adapter.outbound.photographer.PhotographerActiveRegionJpaEntity
import com.damaba.damaba.adapter.outbound.photographer.PhotographerAddressJpaEmbeddable
import com.damaba.damaba.adapter.outbound.photographer.PhotographerJpaEntity
import com.damaba.damaba.adapter.outbound.photographer.PhotographerPortfolioImageJpaEntity
import com.damaba.damaba.domain.common.Address
import com.damaba.damaba.domain.common.PhotographyType
import com.damaba.damaba.domain.photographer.BusinessSchedule
import com.damaba.damaba.domain.photographer.Photographer
import com.damaba.damaba.domain.promotion.Promotion
import com.damaba.damaba.domain.promotion.constant.EventType
import com.damaba.damaba.domain.promotion.constant.PromotionType
import com.damaba.damaba.domain.region.Region
import com.damaba.damaba.domain.region.RegionGroup
import com.damaba.damaba.util.RandomTestUtils.Companion.generateRandomList
import com.damaba.damaba.util.RandomTestUtils.Companion.generateRandomSet
import com.damaba.damaba.util.RandomTestUtils.Companion.randomLocalDate
import com.damaba.damaba.util.RandomTestUtils.Companion.randomLocalTime
import com.damaba.damaba.util.RandomTestUtils.Companion.randomLong
import com.damaba.damaba.util.RandomTestUtils.Companion.randomString
import com.damaba.damaba.util.RandomTestUtils.Companion.randomUrl
import com.damaba.user.adapter.outbound.user.UserJpaEntity
import com.damaba.user.adapter.outbound.user.UserProfileImageJpaEmbeddable
import com.damaba.user.domain.user.User
import com.damaba.user.domain.user.constant.Gender
import com.damaba.user.domain.user.constant.LoginType
import com.damaba.user.domain.user.constant.UserRoleType
import com.damaba.user.domain.user.constant.UserType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

object TestFixture {
    fun createFile(name: String = randomString(), url: String = randomString()) = File(name, url)

    fun createImageRequest(name: String = randomString(), url: String = randomString()) = ImageRequest(name, url)

    fun createImage(name: String = randomString(), url: String = randomString()) = Image(name, url)

    fun createRegion(category: String = randomString(), name: String = randomString()) = Region(category, name)

    fun createRegionRequest(
        category: String = randomString(),
        name: String = randomString(),
    ) = RegionRequest(category, name)

    fun createAddress(
        sido: String = randomString(),
        sigungu: String = randomString(),
        roadAddress: String = randomString(),
        jibunAddress: String = randomString(),
    ) = Address(sido, sigungu, roadAddress, jibunAddress)

    fun createUser(
        id: Long = randomLong(),
        type: UserType = UserType.USER,
        roles: Set<UserRoleType> = setOf(UserRoleType.USER),
        oAuthLoginUid: String = randomString(),
        loginType: LoginType = LoginType.KAKAO,
        nickname: String = randomString(len = 7),
        profileImage: Image = Image(randomString(), randomUrl()),
        gender: Gender = Gender.MALE,
        instagramId: String = randomString(len = 30),
    ): User = User(
        id = id,
        type = type,
        roles = roles,
        oAuthLoginUid = oAuthLoginUid,
        loginType = loginType,
        nickname = nickname,
        profileImage = profileImage,
        gender = gender,
        instagramId = instagramId,
    )

    fun createUserJpaEntity(
        type: UserType = UserType.USER,
        roles: Set<UserRoleType> = setOf(UserRoleType.USER),
        oAuthLoginUid: String = randomString(),
        loginType: LoginType = LoginType.KAKAO,
        nickname: String = randomString(len = 7),
        profileImage: UserProfileImageJpaEmbeddable = UserProfileImageJpaEmbeddable(randomString(), randomUrl()),
        gender: Gender = Gender.MALE,
        instagramId: String = randomString(len = 30),
    ) = UserJpaEntity(
        type = type,
        roles = roles,
        oAuthLoginUid = oAuthLoginUid,
        loginType = loginType,
        nickname = nickname,
        profileImage = profileImage,
        gender = gender,
        instagramId = instagramId,
    )

    fun createAuthenticationToken(user: User): Authentication =
        UsernamePasswordAuthenticationToken(
            user,
            null,
            user.roles
                .map { roleType -> "ROLE_$roleType" }
                .map { roleName -> SimpleGrantedAuthority(roleName) }
                .toMutableList(),
        )

    fun createPhotographer(
        id: Long = randomLong(),
        type: UserType = UserType.PHOTOGRAPHER,
        roles: Set<UserRoleType> = setOf(UserRoleType.USER),
        oAuthLoginUid: String = randomString(),
        loginType: LoginType = LoginType.KAKAO,
        nickname: String = randomString(len = 7),
        profileImage: Image = Image(randomString(), randomUrl()),
        gender: Gender = Gender.MALE,
        instagramId: String = randomString(len = 30),
        mainPhotographyTypes: Set<PhotographyType> = setOf(
            PhotographyType.PROFILE,
            PhotographyType.SELF,
            PhotographyType.ID_PHOTO,
        ),
        contactLink: String? = randomString(),
        description: String? = randomString(),
        address: Address? = createAddress(),
        businessSchedule: BusinessSchedule? = createBusinessSchedule(),
        portfolio: List<Image> = generateRandomList(maxSize = 10) { createImage() },
        activeRegions: Set<Region> = generateRandomSet(maxSize = 3) { createRegion() },
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
        businessSchedule = businessSchedule,
        portfolio = portfolio,
        activeRegions = activeRegions,
    )

    fun createPhotographerJpaEntity(
        id: Long = randomLong(),
        mainPhotographyTypes: Set<PhotographyType> = setOf(
            PhotographyType.PROFILE,
            PhotographyType.SELF,
            PhotographyType.ID_PHOTO,
        ),
        contactLink: String? = randomString(),
        description: String? = randomString(),
        address: PhotographerAddressJpaEmbeddable? = PhotographerAddressJpaEmbeddable(
            sido = randomString(),
            sigungu = randomString(),
            roadAddress = randomString(),
            jibunAddress = randomString(),
        ),
        businessSchedule: BusinessScheduleJpaEmbeddable? = BusinessScheduleJpaEmbeddable(
            days = setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY),
            startTime = randomLocalTime(),
            endTime = randomLocalTime(),
        ),
        portfolio: List<Image> = generateRandomList(maxSize = 10) { createImage() },
        activeRegions: Set<Region> = generateRandomSet(maxSize = 3) { createRegion() },
    ): PhotographerJpaEntity {
        val photographerJpaEntity = PhotographerJpaEntity(
            userId = id,
            mainPhotographyTypes = mainPhotographyTypes,
            contactLink = contactLink,
            description = description,
            address = address,
            businessSchedule = businessSchedule,
        )
        photographerJpaEntity.addPortfolioImages(
            portfolio.map { image ->
                PhotographerPortfolioImageJpaEntity(photographerJpaEntity, image.name, image.url)
            },
        )
        photographerJpaEntity.activeRegions.addAll(
            activeRegions.map { region ->
                PhotographerActiveRegionJpaEntity(photographerJpaEntity, region.category, region.name)
            },
        )
        return photographerJpaEntity
    }

    private fun createBusinessSchedule(
        days: Set<DayOfWeek> = setOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY),
        startTime: LocalTime = randomLocalTime(),
        endTime: LocalTime = randomLocalTime(),
    ) = BusinessSchedule(
        days = days,
        startTime = startTime,
        endTime = endTime,
    )

    fun createPromotion(
        id: Long = randomLong(),
        authorId: Long? = randomLong(),
        type: PromotionType = PromotionType.EVENT,
        eventType: EventType = EventType.FREE,
        title: String = randomString(10),
        content: String = randomString(30),
        address: Address = createAddress(),
        externalLink: String? = null,
        startedAt: LocalDate? = randomLocalDate(),
        endedAt: LocalDate? = randomLocalDate(),
        photographerName: String? = randomString(),
        photographerInstagramId: String? = randomString(),
        images: List<Image>? = null,
        activeRegions: Set<Region>? = null,
        hashtags: Set<String>? = null,
    ): Promotion = Promotion(
        id = id,
        authorId = authorId,
        type = type,
        eventType = eventType,
        title = title,
        content = content,
        address = address,
        externalLink = externalLink,
        startedAt = startedAt,
        endedAt = endedAt,
        photographerName = photographerName,
        photographerInstagramId = photographerInstagramId,
        images = images ?: generateRandomList(maxSize = 10) { createImage() },
        activeRegions = activeRegions ?: generateRandomSet(maxSize = 5) { createRegion() },
        hashtags = hashtags ?: generateRandomSet(maxSize = 5) { randomString() },
    )

    private fun createRegionGroup(): RegionGroup = RegionGroup(
        category = randomString(),
        regions = generateRandomList(maxSize = 10) { randomString() },
    )

    fun createRegionGroups(): List<RegionGroup> =
        generateRandomList(maxSize = 10) { createRegionGroup() }
}
