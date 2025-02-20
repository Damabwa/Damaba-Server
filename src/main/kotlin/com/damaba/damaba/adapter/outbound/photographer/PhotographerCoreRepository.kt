package com.damaba.damaba.adapter.outbound.photographer

import com.damaba.damaba.adapter.outbound.user.UserJpaRepository
import com.damaba.damaba.adapter.outbound.user.UserProfileImageJpaEntity
import com.damaba.damaba.adapter.outbound.user.UserProfileImageJpaRepository
import com.damaba.damaba.application.port.outbound.photographer.CreatePhotographerPort
import com.damaba.damaba.application.port.outbound.photographer.GetPhotographerPort
import com.damaba.damaba.domain.photographer.Photographer
import com.damaba.damaba.domain.photographer.exception.PhotographerNotFoundException
import com.damaba.damaba.domain.user.exception.UserNotFoundException
import org.springframework.stereotype.Repository

@Repository
class PhotographerCoreRepository(
    private val photographerJpaRepository: PhotographerJpaRepository,
    private val userJpaRepository: UserJpaRepository,
    private val userProfileImageJpaRepository: UserProfileImageJpaRepository,
) : GetPhotographerPort,
    CreatePhotographerPort {
    override fun getById(id: Long): Photographer {
        val userJpaEntity = userJpaRepository.findById(id)
            .orElseThrow { PhotographerNotFoundException() }
        val photographerJpaEntity = photographerJpaRepository.findById(id)
            .orElseThrow { PhotographerNotFoundException() }
        return photographerJpaEntity.toPhotographer(userJpaEntity)
    }

    override fun createIfUserExists(photographer: Photographer): Photographer {
        // Update user
        val userJpaEntity = userJpaRepository
            .findById(photographer.id)
            .orElseThrow { UserNotFoundException() }

        val originalProfileImage = userJpaEntity.profileImage
        deleteProfileImageIfExists(originalProfileImage.url)

        userJpaEntity.update(photographer)

        // Create photographer
        userProfileImageJpaRepository.save(
            UserProfileImageJpaEntity(
                userId = photographer.id,
                name = photographer.profileImage.name,
                url = photographer.profileImage.url,
            ),
        )

        val photographerJpaEntity = PhotographerJpaEntity.from(photographer)
        photographerJpaRepository.save(photographerJpaEntity)
        return photographerJpaEntity.toPhotographer(userJpaEntity)
    }

    private fun deleteProfileImageIfExists(imageUrl: String) {
        val profileImage = userProfileImageJpaRepository.findByUrl(imageUrl)
        profileImage?.delete()
    }
}
