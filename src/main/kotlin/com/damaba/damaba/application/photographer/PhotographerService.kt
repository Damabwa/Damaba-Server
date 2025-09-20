package com.damaba.damaba.application.photographer

import com.damaba.damaba.application.term.TermItem
import com.damaba.damaba.domain.common.Pagination
import com.damaba.damaba.domain.photographer.Photographer
import com.damaba.damaba.domain.photographer.PhotographerDetail
import com.damaba.damaba.domain.photographer.PhotographerListItem
import com.damaba.damaba.domain.photographer.PhotographerSave
import com.damaba.damaba.domain.photographer.exception.AlreadyPhotographerSaveException
import com.damaba.damaba.domain.photographer.exception.PhotographerSaveNotFoundException
import com.damaba.damaba.domain.term.Term
import com.damaba.damaba.domain.user.User
import com.damaba.damaba.domain.user.exception.NicknameAlreadyExistsException
import com.damaba.damaba.domain.user.exception.UserAlreadyRegisteredException
import com.damaba.damaba.infrastructure.photographer.PhotographerRepository
import com.damaba.damaba.infrastructure.photographer.PhotographerSaveRepository
import com.damaba.damaba.infrastructure.promotion.PromotionRepository
import com.damaba.damaba.infrastructure.promotion.PromotionSaveRepository
import com.damaba.damaba.infrastructure.term.TermRepository
import com.damaba.damaba.infrastructure.user.UserRepository
import com.damaba.damaba.mapper.PhotographerMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PhotographerService(
    private val userRepo: UserRepository,
    private val photographerRepo: PhotographerRepository,
    private val photographerSaveRepo: PhotographerSaveRepository,
    private val promotionRepo: PromotionRepository,
    private val promotionSaveRepo: PromotionSaveRepository,
    private val termRepo: TermRepository,
) {
    @Transactional
    fun register(command: RegisterPhotographerCommand): Photographer {
        val user: User = userRepo.getById(command.userId)
        if (user.isRegistrationCompleted) {
            throw UserAlreadyRegisteredException()
        }

        if (userRepo.existsNickname(command.nickname)) {
            throw NicknameAlreadyExistsException(command.nickname)
        }

        val photographer = Photographer.create(user)
        photographer.registerPhotographer(
            nickname = command.nickname,
            gender = command.gender,
            instagramId = command.instagramId,
            profileImage = command.profileImage,
            mainPhotographyTypes = command.mainPhotographyTypes,
            activeRegions = command.activeRegions,
        )
        val saved = photographerRepo.createIfUserExists(photographer)

        if (command.terms.isNotEmpty()) {
            val termList: List<Term> = command.terms.map { item: TermItem ->
                Term(
                    id = null,
                    userId = saved.id,
                    type = item.type,
                    agreed = item.agreed,
                )
            }
            termRepo.saveAll(termList)
        }

        return saved
    }

    @Transactional
    fun savePhotographer(command: SavePhotographerCommand) {
        if (photographerSaveRepo.existsByUserIdAndPhotographerId(command.requestUserId, command.photographerId)) {
            throw AlreadyPhotographerSaveException()
        }
        photographerSaveRepo.create(
            PhotographerSave.create(userId = command.requestUserId, photographerId = command.photographerId),
        )
    }

    @Transactional(readOnly = true)
    fun getPhotographer(id: Long): Photographer = photographerRepo.getById(id)

    @Transactional(readOnly = true)
    fun getPhotographerDetail(query: GetPhotographerDetailQuery): PhotographerDetail {
        val photographer = photographerRepo.getById(query.photographerId)

        val saveCount = photographerSaveRepo.countByPhotographerId(query.photographerId)
        val isSaved = if (query.requestUserId != null) {
            photographerSaveRepo.existsByUserIdAndPhotographerId(query.requestUserId, query.photographerId)
        } else {
            false
        }

        return PhotographerDetail(
            id = photographer.id,
            type = photographer.type,
            roles = photographer.roles,
            loginType = photographer.loginType,
            nickname = photographer.nickname,
            profileImage = photographer.profileImage,
            gender = photographer.gender,
            instagramId = photographer.instagramId,
            mainPhotographyTypes = photographer.mainPhotographyTypes,
            contactLink = photographer.contactLink,
            description = photographer.description,
            address = photographer.address,
            portfolio = photographer.portfolio,
            activeRegions = photographer.activeRegions,
            saveCount = saveCount,
            isSaved = isSaved,
        )
    }

    @Transactional(readOnly = true)
    fun findPhotographerList(
        query: FindPhotographerListQuery,
    ): Pagination<PhotographerListItem> = photographerRepo.findPhotographerList(
        requestUserId = query.requestUserId,
        regions = query.regions,
        photographyTypes = query.photographyTypes,
        searchKeyword = query.searchKeyword,
        sort = query.sort,
        page = query.page,
        pageSize = query.pageSize,
    )

    @Transactional(readOnly = true)
    fun findSavedPhotographerList(
        query: FindSavedPhotographerListQuery,
    ): Pagination<PhotographerListItem> = photographerRepo.findSavedPhotographerList(
        requestUserId = query.requestUserId,
        page = query.page,
        pageSize = query.pageSize,
    )

    @Transactional(readOnly = true)
    fun existsNickname(query: ExistsPhotographerNicknameQuery): Boolean = userRepo.existsNickname(query.nickname)

    @Transactional
    fun updatePhotographerProfile(command: UpdatePhotographerProfileCommand): Photographer {
        val photographer = photographerRepo.getById(command.photographerId)

        if (photographer.nickname != command.nickname && userRepo.existsNickname(command.nickname)) {
            throw NicknameAlreadyExistsException(command.nickname)
        }
        photographer.profileImage?.let { originalProfileImage ->
            if (originalProfileImage != command.profileImage) {
                userRepo.deleteProfileImageByUrl(originalProfileImage.url)
            }
        }

        photographer.updateProfile(PhotographerMapper.INSTANCE.toPhotographerProfile(command))
        return photographerRepo.update(photographer)
    }

    @Transactional
    fun updatePhotographerPage(command: UpdatePhotographerPageCommand): Photographer {
        val photographer = photographerRepo.getById(command.photographerId)
        photographer.updatePage(PhotographerMapper.INSTANCE.toPhotographerPage(command))
        return photographerRepo.update(photographer)
    }

    @Transactional
    fun deletePhotographer(photographerId: Long) {
        promotionRepo.findPromotionsByAuthorId(photographerId)
            .forEach { promotion ->
                promotion.removeAuthor()
                promotionRepo.update(promotion)
            }

        val photographer = photographerRepo.getById(photographerId)
        photographerSaveRepo.deleteAllByUserId(photographerId)
        promotionSaveRepo.deleteAllByUserId(photographerId)
        photographerRepo.delete(photographer)
    }

    @Transactional
    fun unsavePhotographer(command: UnsavePhotographerCommand) {
        val photographerSave =
            photographerSaveRepo.findByUserIdAndPhotographerId(command.requestUserId, command.photographerId)
                ?: throw PhotographerSaveNotFoundException()
        photographerSaveRepo.delete(photographerSave)
    }
}
