package com.damaba.damaba.adapter.outbound.photographer

import org.springframework.data.jpa.repository.JpaRepository

interface PhotographerSaveJpaRepository : JpaRepository<PhotographerSaveJpaEntity, Long> {
    fun findByUserIdAndPhotographerId(userId: Long, photographerId: Long): PhotographerSaveJpaEntity?
    fun existsByUserIdAndPhotographerId(userId: Long, photographerId: Long): Boolean
}
