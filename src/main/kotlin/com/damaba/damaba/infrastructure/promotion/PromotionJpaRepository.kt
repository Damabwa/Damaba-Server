package com.damaba.damaba.infrastructure.promotion

import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import java.util.Optional

interface PromotionJpaRepository :
    JpaRepository<PromotionJpaEntity, Long>,
    KotlinJdslJpqlExecutor {
    @Query("SELECT p From PromotionJpaEntity p WHERE p.deletedAt IS NULL AND p.id = :id")
    override fun findById(id: Long): Optional<PromotionJpaEntity>

    @Query("SELECT p From PromotionJpaEntity p WHERE p.deletedAt IS NULL AND p.authorId = :authorId")
    fun findAllByAuthorId(authorId: Long): List<PromotionJpaEntity>

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT 1 FROM PromotionJpaEntity p WHERE p.deletedAt IS NULL AND p.id = :id")
    fun acquirePessimisticLockById(id: Long)
}
