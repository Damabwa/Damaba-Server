package com.damaba.damaba.domain.promotion

import com.damaba.damaba.domain.common.PhotographyType
import com.damaba.damaba.domain.file.Image
import com.damaba.damaba.domain.promotion.constant.PromotionType
import com.damaba.damaba.domain.region.Region
import java.time.LocalDate

class Promotion(
    val id: Long,
    val authorId: Long?,
    val promotionType: PromotionType,
    val title: String,
    val content: String,
    val externalLink: String?,
    val startedAt: LocalDate?,
    val endedAt: LocalDate?,
    viewCount: Long,
    val photographyTypes: Set<PhotographyType>,
    val images: List<Image>,
    val activeRegions: Set<Region>,
    val hashtags: Set<String>,
) {
    var viewCount: Long = viewCount
        private set

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Promotion) return false
        return this.id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    fun incrementViewCount() {
        this.viewCount++
    }

    companion object {
        fun create(
            authorId: Long,
            promotionType: PromotionType,
            title: String,
            content: String,
            externalLink: String?,
            startedAt: LocalDate?,
            endedAt: LocalDate?,
            photographyTypes: Set<PhotographyType>,
            images: List<Image>,
            activeRegions: Set<Region>,
            hashtags: Set<String>,
        ): Promotion = Promotion(
            id = 0,
            authorId = authorId,
            promotionType = promotionType,
            title = title,
            content = content,
            externalLink = externalLink,
            startedAt = startedAt,
            endedAt = endedAt,
            viewCount = 0,
            photographyTypes = photographyTypes,
            images = images,
            activeRegions = activeRegions,
            hashtags = hashtags,
        )
    }
}
