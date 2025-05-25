package com.damaba.damaba.infrastructure.photographer

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

@Entity
@Table(name = "photographer_photography_type")
class PhotographerPhotographyTypeJpaEntity(
    photographer: PhotographerJpaEntity,
    photographyType: PhotographyType,
) : TimeTrackedJpaEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    val id: Long = 0

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photographer_id", nullable = false)
    var photographer: PhotographerJpaEntity = photographer
        private set

    @Column(name = "photography_type", nullable = false)
    @Enumerated(EnumType.STRING)
    var photographyType: PhotographyType = photographyType
        private set
}
