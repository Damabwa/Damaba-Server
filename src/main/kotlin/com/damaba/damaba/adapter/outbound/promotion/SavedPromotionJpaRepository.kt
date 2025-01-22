package com.damaba.damaba.adapter.outbound.promotion

import org.springframework.data.jpa.repository.JpaRepository

interface SavedPromotionJpaRepository : JpaRepository<SavedPromotionJpaEntity, Long> {
    fun existsByUserIdAndPromotionId(userId: Long, promotionId: Long): Boolean
}
