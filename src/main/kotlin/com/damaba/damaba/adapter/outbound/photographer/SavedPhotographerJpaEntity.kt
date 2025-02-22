package com.damaba.damaba.adapter.outbound.photographer

import com.damaba.damaba.adapter.outbound.common.BaseJpaTimeEntity
import com.damaba.damaba.domain.photographer.SavedPhotographer
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "saved_photographer")
class SavedPhotographerJpaEntity(
    userId: Long,
    photographerId: Long,
) : BaseJpaTimeEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    val id: Long = 0

    @Column(name = "user_id", nullable = false)
    var userId: Long = userId
        private set

    @Column(name = "photographer_id", nullable = false)
    var photographerId: Long = photographerId
        private set

    fun toSavedPhotographer() = SavedPhotographer(
        id = this.id,
        userId = this.userId,
        photographerId = this.photographerId,
    )
}
