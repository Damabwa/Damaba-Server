package com.damaba.damaba.util.fixture

import com.damaba.damaba.domain.common.Address
import com.damaba.damaba.domain.common.PhotographyType
import com.damaba.damaba.domain.file.Image
import com.damaba.damaba.domain.promotion.Promotion
import com.damaba.damaba.domain.promotion.PromotionDetail
import com.damaba.damaba.domain.promotion.constant.PromotionType
import com.damaba.damaba.domain.region.Region
import com.damaba.damaba.domain.user.User
import com.damaba.damaba.util.RandomTestUtils.Companion.generateRandomList
import com.damaba.damaba.util.RandomTestUtils.Companion.generateRandomSet
import com.damaba.damaba.util.RandomTestUtils.Companion.randomBoolean
import com.damaba.damaba.util.RandomTestUtils.Companion.randomInt
import com.damaba.damaba.util.RandomTestUtils.Companion.randomLocalDate
import com.damaba.damaba.util.RandomTestUtils.Companion.randomLong
import com.damaba.damaba.util.RandomTestUtils.Companion.randomString
import com.damaba.damaba.util.fixture.AddressFixture.createAddress
import com.damaba.damaba.util.fixture.FileFixture.createImage
import com.damaba.damaba.util.fixture.RegionFixture.createRegion
import com.damaba.damaba.util.fixture.UserFixture.createUser
import java.time.LocalDate

object PromotionFixture {
    fun createPromotion(
        id: Long = randomLong(),
        authorId: Long? = randomLong(),
        promotionType: PromotionType = PromotionType.FREE,
        title: String = randomString(10),
        content: String = randomString(30),
        address: Address = createAddress(),
        externalLink: String? = null,
        startedAt: LocalDate? = randomLocalDate(),
        endedAt: LocalDate? = randomLocalDate(),
        viewCount: Long = randomLong(),
        photographyTypes: Set<PhotographyType> = setOf(PhotographyType.SNAP),
        images: List<Image> = generateRandomList(maxSize = 10) { createImage() },
        activeRegions: Set<Region> = generateRandomSet(maxSize = 5) { createRegion() },
        hashtags: Set<String> = generateRandomSet(maxSize = 5) { randomString() },
    ): Promotion = Promotion(
        id = id,
        authorId = authorId,
        promotionType = promotionType,
        title = title,
        content = content,
        address = address,
        externalLink = externalLink,
        startedAt = startedAt,
        endedAt = endedAt,
        viewCount = viewCount,
        photographyTypes = photographyTypes,
        images = images,
        activeRegions = activeRegions,
        hashtags = hashtags,
    )

    fun createPromotionDetail(
        id: Long = randomLong(),
        author: User? = createUser(),
        promotionType: PromotionType = PromotionType.FREE,
        title: String = randomString(10),
        content: String = randomString(30),
        address: Address = createAddress(),
        externalLink: String? = null,
        startedAt: LocalDate? = randomLocalDate(),
        endedAt: LocalDate? = randomLocalDate(),
        viewCount: Long = randomLong(),
        saveCount: Int = randomInt(),
        isSaved: Boolean = randomBoolean(),
        photographyTypes: Set<PhotographyType> = setOf(PhotographyType.SNAP),
        images: List<Image> = generateRandomList(maxSize = 10) { createImage() },
        activeRegions: Set<Region> = generateRandomSet(maxSize = 5) { createRegion() },
        hashtags: Set<String> = generateRandomSet(maxSize = 5) { randomString() },
    ) = PromotionDetail(
        id = id,
        author = author,
        promotionType = promotionType,
        title = title,
        content = content,
        address = address,
        externalLink = externalLink,
        startedAt = startedAt,
        endedAt = endedAt,
        viewCount = viewCount,
        saveCount = saveCount,
        isSaved = isSaved,
        photographyTypes = photographyTypes,
        images = images,
        activeRegions = activeRegions,
        hashtags = hashtags,
    )
}
