package com.damaba.damaba.adapter.outbound.promotion

import com.damaba.damaba.adapter.outbound.common.BaseJpaTimeEntity
import com.damaba.damaba.domain.promotion.PromotionActiveRegion
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Table(name = "promotion_active_region")
@Entity
class PromotionActiveRegionJpaEntity(
    promotion: PromotionJpaEntity,
    category: String,
    name: String,
) : BaseJpaTimeEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    val id: Long = 0

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_id", nullable = false)
    var promotion: PromotionJpaEntity = promotion
        private set

    @Column(name = "category", nullable = false)
    var category: String = category
        private set

    @Column(name = "name", nullable = false)
    var name: String = name
        private set

    fun toDomain(): PromotionActiveRegion = PromotionActiveRegion(
        category = this.category,
        name = this.name,
    )

    companion object {
        fun from(
            promotionActiveRegion: PromotionActiveRegion,
            promotionJpaEntity: PromotionJpaEntity,
        ): PromotionActiveRegionJpaEntity = PromotionActiveRegionJpaEntity(
            promotion = promotionJpaEntity,
            category = promotionActiveRegion.category,
            name = promotionActiveRegion.name,
        )
    }
}
