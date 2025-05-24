package com.damaba.damaba.infrastructure.photographer

import com.damaba.damaba.domain.photographer.PhotographerSave
import org.springframework.stereotype.Repository

@Repository
class PhotographerSaveCoreRepository(
    private val photographerSaveJpaRepository: PhotographerSaveJpaRepository,
) : PhotographerSaveRepository {
    override fun findByUserIdAndPhotographerId(
        userId: Long,
        photographerId: Long,
    ): PhotographerSave? = photographerSaveJpaRepository.findByUserIdAndPhotographerId(userId, photographerId)?.toPhotographerSave()

    override fun existsByUserIdAndPhotographerId(
        userId: Long,
        photographerId: Long,
    ): Boolean = photographerSaveJpaRepository.existsByUserIdAndPhotographerId(userId, photographerId)

    override fun create(photographerSave: PhotographerSave) {
        photographerSaveJpaRepository.save(
            PhotographerSaveJpaEntity(
                userId = photographerSave.userId,
                photographerId = photographerSave.photographerId,
            ),
        )
    }

    override fun delete(photographerSave: PhotographerSave) {
        photographerSaveJpaRepository.deleteById(photographerSave.id)
    }
}
