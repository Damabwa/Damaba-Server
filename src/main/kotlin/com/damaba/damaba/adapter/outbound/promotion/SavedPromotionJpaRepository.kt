package com.damaba.damaba.adapter.outbound.promotion

import org.springframework.data.jpa.repository.JpaRepository

interface SavedPromotionJpaRepository : JpaRepository<SavedPromotionJpaEntity, Long> {
    fun findByUserIdAndPromotionId(userId: Long, promotionId: Long): SavedPromotionJpaEntity?
    fun existsByUserIdAndPromotionId(userId: Long, promotionId: Long): Boolean
    fun countByPromotionId(promotionId: Long): Long
}
