package com.damaba.damaba.adapter.outbound.promotion

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.Optional

interface PromotionJpaRepository : JpaRepository<PromotionJpaEntity, Long> {
    @Query("SELECT promotion From PromotionJpaEntity promotion WHERE promotion.deletedAt IS NULL AND promotion.id = :id")
    override fun findById(id: Long): Optional<PromotionJpaEntity>

    @Query("SELECT promotion FROM PromotionJpaEntity promotion WHERE promotion.deletedAt IS NULL")
    override fun findAll(pageable: Pageable): Page<PromotionJpaEntity>
}
