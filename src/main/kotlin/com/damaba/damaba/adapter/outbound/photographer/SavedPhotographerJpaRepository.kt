package com.damaba.damaba.adapter.outbound.photographer

import org.springframework.data.jpa.repository.JpaRepository

interface SavedPhotographerJpaRepository : JpaRepository<SavedPhotographerJpaEntity, Long> {
    fun existsByUserIdAndPhotographerId(userId: Long, photographerId: Long): Boolean
}
