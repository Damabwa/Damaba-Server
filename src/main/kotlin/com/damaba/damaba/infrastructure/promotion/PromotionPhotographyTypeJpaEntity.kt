package com.damaba.damaba.infrastructure.promotion

import com.damaba.damaba.domain.common.constant.PhotographyType
import com.damaba.damaba.infrastructure.common.TimeTrackedJpaEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Table(name = "promotion_photography_type")
@Entity
class PromotionPhotographyTypeJpaEntity(
    promotion: PromotionJpaEntity,
    type: PhotographyType,
) : TimeTrackedJpaEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    val id: Long = 0

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_id", nullable = false)
    var promotion: PromotionJpaEntity = promotion
        private set

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    var type: PhotographyType = type
        private set
}
