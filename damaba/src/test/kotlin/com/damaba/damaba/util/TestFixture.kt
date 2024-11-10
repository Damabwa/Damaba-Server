package com.damaba.damaba.util

import com.damaba.common_file.domain.File
import com.damaba.damaba.domain.common.Address
import com.damaba.damaba.domain.promotion.Promotion
import com.damaba.damaba.domain.promotion.PromotionActiveRegion
import com.damaba.damaba.domain.promotion.PromotionImage
import com.damaba.damaba.domain.promotion.constant.EventType
import com.damaba.damaba.domain.promotion.constant.PromotionType
import com.damaba.damaba.domain.region.Region
import com.damaba.damaba.domain.region.RegionGroup
import com.damaba.damaba.util.RandomTestUtils.Companion.generateRandomList
import com.damaba.damaba.util.RandomTestUtils.Companion.generateRandomSet
import com.damaba.damaba.util.RandomTestUtils.Companion.randomLocalDate
import com.damaba.damaba.util.RandomTestUtils.Companion.randomLong
import com.damaba.damaba.util.RandomTestUtils.Companion.randomString
import com.damaba.user.domain.user.User
import com.damaba.user.domain.user.constant.Gender
import com.damaba.user.domain.user.constant.LoginType
import com.damaba.user.domain.user.constant.UserRoleType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.time.LocalDate

object TestFixture {
    fun createUser(
        id: Long = randomLong(),
        roles: Set<UserRoleType> = setOf(UserRoleType.USER),
        oAuthLoginUid: String = randomString(),
        loginType: LoginType = LoginType.KAKAO,
        nickname: String = randomString(len = 7),
        profileImageUrl: String = randomString(),
        gender: Gender = Gender.MALE,
        instagramId: String = randomString(len = 30),
    ): User = User(
        id = id,
        roles = roles,
        oAuthLoginUid = oAuthLoginUid,
        loginType = loginType,
        nickname = nickname,
        profileImageUrl = profileImageUrl,
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

    fun createAddress(
        sido: String = randomString(),
        sigungu: String = randomString(),
        roadAddress: String = randomString(),
        jibunAddress: String = randomString(),
    ) = Address(sido, sigungu, roadAddress, jibunAddress)

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
        images: List<PromotionImage>? = null,
        activeRegions: Set<PromotionActiveRegion>? = null,
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
        images = images ?: generateRandomList(maxSize = 10) { createPromotionImage() },
        activeRegions = activeRegions ?: generateRandomSet(maxSize = 5) { createPromotionActiveRegion() },
        hashtags = hashtags ?: generateRandomSet(maxSize = 5) { randomString() },
    )

    private fun createPromotionImage(
        name: String = randomString(),
        url: String = randomString(),
    ) = PromotionImage(name, url)

    private fun createPromotionActiveRegion(
        category: String = randomString(),
        name: String = randomString(),
    ) = PromotionActiveRegion(category, name)

    fun createRegion() = Region(
        category = randomString(),
        name = randomString(),
    )

    private fun createRegionGroup(): RegionGroup = RegionGroup(
        category = randomString(),
        regions = generateRandomList(maxSize = 10) { randomString() },
    )

    fun createRegionGroups(): List<RegionGroup> =
        generateRandomList(maxSize = 10) { createRegionGroup() }

    fun createFile(
        name: String = randomString(),
        url: String = randomString(),
    ) = File(name, url)
}
