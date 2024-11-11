package com.damaba.damaba.adapter.outbound.promotion

import com.damaba.damaba.adapter.outbound.common.BaseJpaEntity
import com.damaba.damaba.domain.promotion.PromotionImage
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

@Table(name = "promotion_image")
@Entity
class PromotionImageJpaEntity(
    promotion: PromotionJpaEntity,
    name: String,
    url: String,
) : BaseJpaEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    val id: Long = 0

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_id", nullable = false)
    var promotion: PromotionJpaEntity = promotion
        private set

    @Column(name = "name", unique = true, nullable = false)
    var name: String = name
        private set

    @Column(name = "url", unique = true, nullable = false)
    var url: String = url
        private set

    @Column(name = "deleted_at", nullable = true)
    var deletedAt: LocalDateTime? = null
        private set

    fun toDomain(): PromotionImage = PromotionImage(
        name = this.name,
        url = this.url,
    )

    companion object {
        fun from(
            promotionImage: PromotionImage,
            promotionJpaEntity: PromotionJpaEntity,
        ): PromotionImageJpaEntity = PromotionImageJpaEntity(
            promotion = promotionJpaEntity,
            name = promotionImage.name,
            url = promotionImage.url,
        )
    }
}
