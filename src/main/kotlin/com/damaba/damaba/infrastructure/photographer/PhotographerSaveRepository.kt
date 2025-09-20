package com.damaba.damaba.infrastructure.photographer

import com.damaba.damaba.domain.photographer.PhotographerSave

interface PhotographerSaveRepository {
    fun create(photographerSave: PhotographerSave)

    fun existsByUserIdAndPhotographerId(userId: Long, photographerId: Long): Boolean

    fun findByUserIdAndPhotographerId(userId: Long, photographerId: Long): PhotographerSave?

    fun countByPhotographerId(photographerId: Long): Int

    fun delete(photographerSave: PhotographerSave)

    fun deleteAllByUserId(userId: Long)
}
