package com.damaba.damaba.infrastructure.promotion

import org.springframework.data.jpa.repository.JpaRepository

interface PromotionSaveJpaRepository : JpaRepository<PromotionSaveJpaEntity, Long> {
    fun findByUserIdAndPromotionId(userId: Long, promotionId: Long): PromotionSaveJpaEntity?
    fun existsByUserIdAndPromotionId(userId: Long, promotionId: Long): Boolean
    fun countByPromotionId(promotionId: Long): Long
}
