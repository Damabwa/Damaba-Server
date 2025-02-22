package com.damaba.damaba.adapter.outbound.photographer

import com.damaba.damaba.application.port.outbound.photographer.CreateSavedPhotographerPort
import com.damaba.damaba.application.port.outbound.photographer.DeleteSavedPhotographerPort
import com.damaba.damaba.application.port.outbound.photographer.ExistsSavedPhotographerPort
import com.damaba.damaba.application.port.outbound.photographer.FindSavedPhotographerPort
import com.damaba.damaba.domain.photographer.SavedPhotographer
import org.springframework.stereotype.Repository

@Repository
class SavedPhotographerCoreRepository(
    private val savedPhotographerJpaRepository: SavedPhotographerJpaRepository,
) : FindSavedPhotographerPort,
    ExistsSavedPhotographerPort,
    CreateSavedPhotographerPort,
    DeleteSavedPhotographerPort {
    override fun findByUserIdAndPhotographerId(
        userId: Long,
        photographerId: Long,
    ): SavedPhotographer? = savedPhotographerJpaRepository.findByUserIdAndPhotographerId(userId, photographerId)?.toSavedPhotographer()

    override fun existsByUserIdAndPhotographerId(
        userId: Long,
        photographerId: Long,
    ): Boolean = savedPhotographerJpaRepository.existsByUserIdAndPhotographerId(userId, photographerId)

    override fun create(savedPhotographer: SavedPhotographer) {
        savedPhotographerJpaRepository.save(
            SavedPhotographerJpaEntity(
                userId = savedPhotographer.userId,
                photographerId = savedPhotographer.photographerId,
            ),
        )
    }

    override fun delete(savedPhotographer: SavedPhotographer) {
        savedPhotographerJpaRepository.deleteById(savedPhotographer.id)
    }
}
