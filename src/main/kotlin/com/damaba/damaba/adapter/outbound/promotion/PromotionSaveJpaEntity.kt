package com.damaba.damaba.adapter.outbound.promotion

import com.damaba.damaba.adapter.outbound.common.BaseJpaTimeEntity
import com.damaba.damaba.domain.promotion.PromotionSave
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Table(name = "promotion_save")
@Entity
class PromotionSaveJpaEntity(
    userId: Long,
    promotionId: Long,
) : BaseJpaTimeEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    val id: Long = 0

    @Column(name = "user_id", nullable = false)
    var userId: Long = userId
        private set

    @Column(name = "promotion_id", nullable = false)
    var promotionId: Long = promotionId
        private set

    fun toPromotionSave() = PromotionSave(
        id = this.id,
        userId = this.userId,
        promotionId = this.promotionId,
    )
}
