package com.damaba.damaba.adapter.outbound.promotion

import jakarta.persistence.LockModeType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import java.util.Optional

interface PromotionJpaRepository : JpaRepository<PromotionJpaEntity, Long> {
    @Query("SELECT p From PromotionJpaEntity p WHERE p.deletedAt IS NULL AND p.id = :id")
    override fun findById(id: Long): Optional<PromotionJpaEntity>

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT 1 FROM PromotionJpaEntity p WHERE p.deletedAt IS NULL AND p.id = :id")
    fun acquireLockById(id: Long)

    @Query("SELECT p FROM PromotionJpaEntity p WHERE p.deletedAt IS NULL")
    override fun findAll(pageable: Pageable): Page<PromotionJpaEntity>
}
