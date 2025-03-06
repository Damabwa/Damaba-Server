package com.damaba.damaba.adapter.outbound.photographer

import com.damaba.damaba.adapter.outbound.common.BaseJpaEntity
import com.damaba.damaba.domain.file.Image
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

@Table(name = "photographer_portfolio_image")
@Entity
class PhotographerPortfolioImageJpaEntity(
    photographer: PhotographerJpaEntity,
    name: String,
    url: String,
) : BaseJpaEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    val id: Long = 0

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photographer_id", nullable = false)
    var photographer: PhotographerJpaEntity = photographer
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

    fun toImage() = Image(name = this.name, url = this.url)

    fun delete() {
        this.deletedAt = LocalDateTime.now()
    }

    fun isDeleted(): Boolean = this.deletedAt != null

    companion object {
        fun from(photographerJpaEntity: PhotographerJpaEntity, image: Image) = PhotographerPortfolioImageJpaEntity(
            photographer = photographerJpaEntity,
            name = image.name,
            url = image.url,
        )
    }
}
