package com.damaba.damaba.infrastructure.photographer

import com.damaba.damaba.domain.common.Pagination
import com.damaba.damaba.domain.common.constant.PhotographyType
import com.damaba.damaba.domain.photographer.Photographer
import com.damaba.damaba.domain.photographer.PhotographerListItem
import com.damaba.damaba.domain.photographer.constant.PhotographerSortType
import com.damaba.damaba.domain.photographer.exception.PhotographerNotFoundException
import com.damaba.damaba.domain.region.RegionFilterCondition
import com.damaba.damaba.domain.user.exception.UserNotFoundException
import com.damaba.damaba.infrastructure.common.toPagination
import com.damaba.damaba.infrastructure.user.UserJpaEntity
import com.damaba.damaba.infrastructure.user.UserJpaRepository
import com.damaba.damaba.infrastructure.user.UserProfileImageJpaEntity
import com.damaba.damaba.infrastructure.user.UserProfileImageJpaRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Repository

@Repository
class PhotographerCoreRepository(
    private val photographerJpaRepository: PhotographerJpaRepository,
    private val photographerJdslRepository: PhotographerJdslRepository,
    private val userJpaRepository: UserJpaRepository,
    private val userProfileImageJpaRepository: UserProfileImageJpaRepository,
) : PhotographerRepository {
    override fun getById(id: Long): Photographer {
        val userJpaEntity = getUserJpaEntityById(id)
        val photographerJpaEntity = getPhotographerJpaEntityById(id)
        return photographerJpaEntity.toPhotographer(userJpaEntity)
    }

    override fun findPhotographerList(
        requestUserId: Long?,
        regions: Set<RegionFilterCondition>,
        photographyTypes: Set<PhotographyType>,
        sort: PhotographerSortType,
        page: Int,
        pageSize: Int,
    ): Pagination<PhotographerListItem> = photographerJdslRepository.findPhotographerList(
        requestUserId = requestUserId,
        regions = regions,
        photographyTypes = photographyTypes,
        sort = sort,
        pageable = PageRequest.of(page, pageSize),
    ).toPagination()

    override fun findSavedPhotographerList(
        requestUserId: Long?,
        page: Int,
        pageSize: Int,
    ): Pagination<PhotographerListItem> = photographerJdslRepository.findSavedPhotographerList(
        requestUserId = requestUserId,
        pageable = PageRequest.of(page, pageSize),
    ).toPagination()

    override fun createIfUserExists(photographer: Photographer): Photographer {
        // Update user
        val userJpaEntity = getUserJpaEntityById(photographer.id)

        userJpaEntity.profileImage?.let { originalProfileImage ->
            deleteProfileImageIfExists(originalProfileImage.url)
        }

        userJpaEntity.update(photographer)

        // Create photographer
        photographer.profileImage?.let { profileImage ->
            userProfileImageJpaRepository.save(
                UserProfileImageJpaEntity(
                    userId = photographer.id,
                    name = profileImage.name,
                    url = profileImage.url,
                ),
            )
        }

        val photographerJpaEntity = PhotographerJpaEntity.from(photographer)
        photographerJpaRepository.save(photographerJpaEntity)
        return photographerJpaEntity.toPhotographer(userJpaEntity)
    }

    override fun update(photographer: Photographer): Photographer {
        val userJpaEntity = getUserJpaEntityById(photographer.id)
        val photographerJpaEntity = getPhotographerJpaEntityById(photographer.id)

        if (userJpaEntity.profileImage?.url != photographer.profileImage?.url) {
            userJpaEntity.profileImage?.let { originalProfileImage ->
                deleteProfileImageIfExists(originalProfileImage.url)
            }
            photographer.profileImage?.let { newProfileImage ->
                userProfileImageJpaRepository.save(
                    UserProfileImageJpaEntity(
                        userId = photographer.id,
                        name = newProfileImage.name,
                        url = newProfileImage.url,
                    ),
                )
            }
        }

        userJpaEntity.update(photographer)
        photographerJpaEntity.update(photographer)

        return photographerJpaEntity.toPhotographer(userJpaEntity)
    }

    override fun delete(photographer: Photographer) {
        val photographerJpaEntity = getPhotographerJpaEntityById(photographer.id)
        photographerJpaRepository.delete(photographerJpaEntity)

        val profileImage = photographer.profileImage
        if (profileImage != null) {
            userProfileImageJpaRepository.findByUrl(profileImage.url)?.delete()
        }

        val userJpaEntity = getUserJpaEntityById(photographer.id)
        userJpaRepository.delete(userJpaEntity)
    }

    private fun getUserJpaEntityById(id: Long): UserJpaEntity = userJpaRepository.findById(id).orElseThrow { UserNotFoundException() }

    private fun getPhotographerJpaEntityById(id: Long): PhotographerJpaEntity = photographerJpaRepository.findById(id).orElseThrow { PhotographerNotFoundException() }

    private fun deleteProfileImageIfExists(imageUrl: String) {
        userProfileImageJpaRepository.findByUrl(imageUrl)?.delete()
    }
}
