package com.damaba.damaba.infrastructure.photographer

import org.springframework.data.jpa.repository.JpaRepository

interface PhotographerSaveJpaRepository : JpaRepository<PhotographerSaveJpaEntity, Long> {
    fun findByUserIdAndPhotographerId(userId: Long, photographerId: Long): PhotographerSaveJpaEntity?
    fun existsByUserIdAndPhotographerId(userId: Long, photographerId: Long): Boolean
    fun deleteAllByUserId(userId: Long)
}
