package com.damaba.damaba.adapter.outbound.promotion

import com.damaba.damaba.adapter.outbound.common.AddressJpaEmbeddable
import com.damaba.damaba.adapter.outbound.common.BaseJpaTimeEntity
import com.damaba.damaba.domain.promotion.Promotion
import com.damaba.damaba.domain.promotion.constant.EventType
import com.damaba.damaba.domain.promotion.constant.PromotionType
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Embedded
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
    type: PromotionType,
    eventType: EventType,
    title: String,
    content: String,
    address: AddressJpaEmbeddable,
    externalLink: String?,
    startedAt: LocalDate?,
    endedAt: LocalDate?,
    photographerName: String?,
    photographerInstagramId: String?,
) : BaseJpaTimeEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    val id: Long = 0

    @Column(name = "author_id")
    var authorId: Long? = authorId
        private set

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    var type: PromotionType = type
        private set

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    var eventType: EventType = eventType
        private set

    @Column(name = "title", nullable = false)
    var title: String = title
        private set

    @Column(name = "content", length = 500, nullable = false)
    var content: String = content
        private set

    @Embedded
    var address: AddressJpaEmbeddable = address
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

    @Column(name = "photographer_name")
    var photographerName: String? = photographerName
        private set

    @Column(name = "photographer_instagram_id")
    var photographerInstagramId: String? = photographerInstagramId
        private set

    @Column(name = "deleted_at")
    var deletedAt: LocalDateTime? = null
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

    fun toDomain(): Promotion = Promotion(
        id = this.id,
        authorId = this.authorId,
        type = this.type,
        eventType = this.eventType,
        title = this.title,
        content = this.content,
        address = this.address.toDomain(),
        externalLink = this.externalLink,
        startedAt = this.startedAt,
        endedAt = this.endedAt,
        photographerName = this.photographerName,
        photographerInstagramId = this.photographerInstagramId,
        images = this.images.map(PromotionImageJpaEntity::toDomain),
        activeRegions = this.activeRegions.map(PromotionActiveRegionJpaEntity::toDomain).toSet(),
        hashtags = this.hashtags.map(PromotionHashtagJpaEntity::content).toSet(),
    )

    companion object {
        fun from(promotion: Promotion): PromotionJpaEntity {
            val promotionJpaEntity = PromotionJpaEntity(
                authorId = promotion.authorId,
                type = promotion.type,
                eventType = promotion.eventType,
                title = promotion.title,
                content = promotion.content,
                address = AddressJpaEmbeddable.from(promotion.address),
                externalLink = promotion.externalLink,
                startedAt = promotion.startedAt,
                endedAt = promotion.endedAt,
                photographerName = promotion.photographerName,
                photographerInstagramId = promotion.photographerInstagramId,
            )

            val promotionImageJpaEntities = promotion.images.map { promotionImage ->
                PromotionImageJpaEntity.from(promotionImage, promotionJpaEntity)
            }
            promotionJpaEntity._images.addAll(promotionImageJpaEntities)

            val promotionActiveRegionJpaEntities = promotion.activeRegions.map { promotionActiveRegion ->
                PromotionActiveRegionJpaEntity.from(promotionActiveRegion, promotionJpaEntity)
            }
            promotionJpaEntity.activeRegions.addAll(promotionActiveRegionJpaEntities)

            val promotionHashtagJpaEntities = promotion.hashtags.map { promotionHashtag ->
                PromotionHashtagJpaEntity.from(promotionHashtag, promotionJpaEntity)
            }
            promotionJpaEntity.hashtags.addAll(promotionHashtagJpaEntities)

            return promotionJpaEntity
        }
    }
}
