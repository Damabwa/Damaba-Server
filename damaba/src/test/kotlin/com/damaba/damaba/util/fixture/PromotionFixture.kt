package com.damaba.damaba.util.fixture

import com.damaba.common_file.domain.Image
import com.damaba.damaba.domain.common.Address
import com.damaba.damaba.domain.promotion.Promotion
import com.damaba.damaba.domain.promotion.constant.EventType
import com.damaba.damaba.domain.promotion.constant.PromotionType
import com.damaba.damaba.domain.region.Region
import com.damaba.damaba.util.RandomTestUtils.Companion.generateRandomList
import com.damaba.damaba.util.RandomTestUtils.Companion.generateRandomSet
import com.damaba.damaba.util.RandomTestUtils.Companion.randomLocalDate
import com.damaba.damaba.util.RandomTestUtils.Companion.randomLong
import com.damaba.damaba.util.RandomTestUtils.Companion.randomString
import com.damaba.damaba.util.fixture.AddressFixture.createAddress
import com.damaba.damaba.util.fixture.FileFixture.createImage
import com.damaba.damaba.util.fixture.RegionFixture.createRegion
import java.time.LocalDate

object PromotionFixture {
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
}
