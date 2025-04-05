package com.damaba.damaba.adapter.outbound.promotion

import com.damaba.damaba.adapter.outbound.common.BaseJpaTimeEntity
import com.damaba.damaba.domain.promotion.Promotion
import com.damaba.damaba.domain.promotion.constant.PromotionType
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.time.LocalDate
import java.time.LocalDateTime

@Table(name = "promotion")
@Entity
class PromotionJpaEntity(
    authorId: Long?,
    promotionType: PromotionType,
    title: String,
    content: String,
    externalLink: String?,
    startedAt: LocalDate?,
    endedAt: LocalDate?,
    viewCount: Long,
) : BaseJpaTimeEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    val id: Long = 0

    @Column(name = "author_id")
    var authorId: Long? = authorId
        private set

    @Enumerated(EnumType.STRING)
    @Column(name = "promotion_type", nullable = false)
    var promotionType: PromotionType = promotionType
        private set

    @Column(name = "title", nullable = false)
    var title: String = title
        private set

    @Column(name = "content", length = 500, nullable = false)
    var content: String = content
        private set

    @Column(name = "external_link")
    var externalLink: String? = externalLink
        private set

    @Column(name = "started_at")
    var startedAt: LocalDate? = startedAt
        private set

    @Column(name = "ended_at")
    var endedAt: LocalDate? = endedAt
        private set

    @Column(name = "view_count")
    var viewCount: Long = viewCount
        private set

    @Column(name = "deleted_at")
    var deletedAt: LocalDateTime? = null
        private set

    @OneToMany(mappedBy = "promotion", cascade = [CascadeType.ALL], orphanRemoval = true)
    var photographyTypes: MutableSet<PromotionPhotographyTypeJpaEntity> = mutableSetOf()
        private set

    @OneToMany(mappedBy = "promotion", cascade = [CascadeType.PERSIST])
    private var _images: MutableList<PromotionImageJpaEntity> = mutableListOf()

    val images: List<PromotionImageJpaEntity>
        get() = _images.filter { image -> image.deletedAt == null }.toList()

    @OneToMany(mappedBy = "promotion", cascade = [CascadeType.ALL], orphanRemoval = true)
    var activeRegions: MutableSet<PromotionActiveRegionJpaEntity> = mutableSetOf()
        private set

    @OneToMany(mappedBy = "promotion", cascade = [CascadeType.ALL], orphanRemoval = true)
    var hashtags: MutableSet<PromotionHashtagJpaEntity> = mutableSetOf()
        private set

    fun toPromotion() = Promotion(
        id = this.id,
        authorId = this.authorId,
        promotionType = this.promotionType,
        title = this.title,
        content = this.content,
        externalLink = this.externalLink,
        startedAt = this.startedAt,
        endedAt = this.endedAt,
        viewCount = this.viewCount,
        photographyTypes = this.photographyTypes.map { it.type }.toSet(),
        images = this.images.map { it.toImage() },
        activeRegions = this.activeRegions.map { it.toRegion() }.toSet(),
        hashtags = this.hashtags.map { it.content }.toSet(),
    )

    fun update(promotion: Promotion) {
        this.authorId = promotion.authorId
        this.promotionType = promotion.promotionType
        this.title = promotion.title
        this.content = promotion.content
        this.externalLink = promotion.externalLink
        this.startedAt = promotion.startedAt
        this.endedAt = promotion.endedAt
        this.viewCount = promotion.viewCount
    }

    fun delete() {
        this.deletedAt = LocalDateTime.now()
    }

    companion object {
        fun from(promotion: Promotion): PromotionJpaEntity {
            val promotionJpaEntity = PromotionJpaEntity(
                authorId = promotion.authorId,
                promotionType = promotion.promotionType,
                title = promotion.title,
                content = promotion.content,
                externalLink = promotion.externalLink,
                startedAt = promotion.startedAt,
                endedAt = promotion.endedAt,
                viewCount = promotion.viewCount,
            )
            promotionJpaEntity.photographyTypes.addAll(
                promotion.photographyTypes.map {
                    PromotionPhotographyTypeJpaEntity(promotion = promotionJpaEntity, type = it)
                },
            )
            promotionJpaEntity._images.addAll(
                promotion.images.map {
                    PromotionImageJpaEntity.from(promotion = promotionJpaEntity, image = it)
                },
            )
            promotionJpaEntity.activeRegions.addAll(
                promotion.activeRegions.map {
                    PromotionActiveRegionJpaEntity.from(promotion = promotionJpaEntity, region = it)
                },
            )
            promotionJpaEntity.hashtags.addAll(
                promotion.hashtags.map {
                    PromotionHashtagJpaEntity(promotion = promotionJpaEntity, content = it)
                },
            )
            return promotionJpaEntity
        }
    }
}
