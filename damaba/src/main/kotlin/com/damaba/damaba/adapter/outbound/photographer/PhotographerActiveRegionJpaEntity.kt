package com.damaba.damaba.adapter.outbound.photographer

import com.damaba.damaba.adapter.outbound.common.BaseJpaTimeEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Table(name = "photographer_active_region")
@Entity
class PhotographerActiveRegionJpaEntity(
    photographer: PhotographerJpaEntity,
    category: String,
    name: String,
) : BaseJpaTimeEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    val id: Long = 0

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photographer_id", nullable = false)
    var photographer: PhotographerJpaEntity = photographer
        private set

    @Column(name = "category", nullable = false)
    var category: String = category
        private set

    @Column(name = "name", nullable = false)
    var name: String = name
        private set
}
