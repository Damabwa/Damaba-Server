package com.damaba.damaba.domain.promotion

import com.damaba.damaba.domain.common.Address
import com.damaba.damaba.domain.file.Image
import com.damaba.damaba.domain.promotion.constant.EventType
import com.damaba.damaba.domain.promotion.constant.PromotionType
import com.damaba.damaba.domain.region.Region
import java.time.LocalDate

class Promotion(
    val id: Long,
    val authorId: Long?,
    val type: PromotionType,
    val eventType: EventType,
    val title: String,
    val content: String,
    val address: Address,
    val externalLink: String?,
    val startedAt: LocalDate?,
    val endedAt: LocalDate?,
    val photographerName: String?,
    val photographerInstagramId: String?,
    val images: List<Image>,
    val activeRegions: Set<Region>,
    val hashtags: Set<String>,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Promotion) return false
        return this.id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    companion object {
        fun create(
            authorId: Long,
            type: PromotionType,
            eventType: EventType,
            title: String,
            content: String,
            address: Address,
            externalLink: String?,
            startedAt: LocalDate?,
            endedAt: LocalDate?,
            photographerName: String?,
            photographerInstagramId: String?,
            images: List<Image>,
            activeRegions: Set<Region>,
            hashtags: Set<String>,
        ): Promotion = Promotion(
            id = 0,
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
            images = images,
            activeRegions = activeRegions,
            hashtags = hashtags,
        )
    }
}
