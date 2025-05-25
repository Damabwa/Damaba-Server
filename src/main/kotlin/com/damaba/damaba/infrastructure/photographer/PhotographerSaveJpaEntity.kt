package com.damaba.damaba.infrastructure.photographer

import com.damaba.damaba.domain.photographer.PhotographerSave
import com.damaba.damaba.infrastructure.common.TimeTrackedJpaEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "photographer_save")
class PhotographerSaveJpaEntity(
    userId: Long,
    photographerId: Long,
) : TimeTrackedJpaEntity() {
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

    fun toPhotographerSave() = PhotographerSave(
        id = this.id,
        userId = this.userId,
        photographerId = this.photographerId,
    )
}
