package com.damaba.damaba.domain.promotion

import com.damaba.damaba.domain.common.constant.PhotographyType
import com.damaba.damaba.domain.file.Image
import com.damaba.damaba.domain.promotion.constant.PromotionType
import com.damaba.damaba.domain.region.Region
import java.time.LocalDate

class Promotion(
    val id: Long,
    authorId: Long?,
    promotionType: PromotionType,
    title: String,
    content: String,
    externalLink: String?,
    startedAt: LocalDate?,
    endedAt: LocalDate?,
    viewCount: Long,
    photographyTypes: Set<PhotographyType>,
    images: List<Image>,
    activeRegions: Set<Region>,
    hashtags: Set<String>,
) {
    var authorId: Long? = authorId
        private set

    var promotionType: PromotionType = promotionType
        private set

    var title: String = title
        private set

    var content: String = content
        private set

    var externalLink: String? = externalLink
        private set

    var startedAt: LocalDate? = startedAt
        private set

    var endedAt: LocalDate? = endedAt
        private set

    var viewCount: Long = viewCount
        private set

    var photographyTypes: Set<PhotographyType> = photographyTypes
        private set

    var images: List<Image> = images
        private set

    var activeRegions: Set<Region> = activeRegions
        private set

    var hashtags: Set<String> = hashtags
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

    fun update(
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
    ) {
        this.promotionType = promotionType
        this.title = title
        this.content = content
        this.externalLink = externalLink
        this.startedAt = startedAt
        this.endedAt = endedAt
        this.photographyTypes = photographyTypes
        this.images = images
        this.activeRegions = activeRegions
        this.hashtags = hashtags
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

    fun removeAuthor() {
        this.authorId = null
    }
}
