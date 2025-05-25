package com.damaba.damaba.application.promotion

import com.damaba.damaba.application.port.inbound.promotion.DeletePromotionUseCase
import com.damaba.damaba.application.port.inbound.promotion.FindPromotionListUseCase
import com.damaba.damaba.application.port.inbound.promotion.FindSavedPromotionListUseCase
import com.damaba.damaba.application.port.inbound.promotion.GetPromotionDetailUseCase
import com.damaba.damaba.application.port.inbound.promotion.GetPromotionUseCase
import com.damaba.damaba.application.port.inbound.promotion.PostPromotionUseCase
import com.damaba.damaba.application.port.inbound.promotion.SavePromotionUseCase
import com.damaba.damaba.application.port.inbound.promotion.UnsavePromotionUseCase
import com.damaba.damaba.application.port.inbound.promotion.UpdatePromotionUseCase
import com.damaba.damaba.domain.common.Pagination
import com.damaba.damaba.domain.common.TransactionalLock
import com.damaba.damaba.domain.common.constant.LockType.PESSIMISTIC
import com.damaba.damaba.domain.file.Image
import com.damaba.damaba.domain.promotion.Promotion
import com.damaba.damaba.domain.promotion.PromotionDetail
import com.damaba.damaba.domain.promotion.PromotionListItem
import com.damaba.damaba.domain.promotion.PromotionSave
import com.damaba.damaba.domain.promotion.exception.AlreadyPromotionSaveException
import com.damaba.damaba.domain.promotion.exception.PromotionDeletePermissionDeniedException
import com.damaba.damaba.domain.promotion.exception.PromotionUpdatePermissionDeniedException
import com.damaba.damaba.domain.region.Region
import com.damaba.damaba.infrastructure.promotion.PromotionRepository
import com.damaba.damaba.infrastructure.promotion.PromotionSaveRepository
import com.damaba.damaba.infrastructure.user.UserRepository
import com.damaba.damaba.mapper.PromotionMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PromotionService(
    private val userRepo: UserRepository,
    private val promotionRepo: PromotionRepository,
    private val promotionSaveRepo: PromotionSaveRepository,
) : GetPromotionUseCase,
    GetPromotionDetailUseCase,
    FindPromotionListUseCase,
    FindSavedPromotionListUseCase,
    PostPromotionUseCase,
    UpdatePromotionUseCase,
    DeletePromotionUseCase,

    SavePromotionUseCase,
    UnsavePromotionUseCase {

    @Transactional(readOnly = true)
    override fun getPromotion(promotionId: Long): Promotion = promotionRepo.getById(promotionId)

    @TransactionalLock(lockType = PESSIMISTIC, domainType = Promotion::class, idFieldName = "query.promotionId")
    override fun getPromotionDetail(query: GetPromotionDetailUseCase.Query): PromotionDetail {
        val promotion = promotionRepo.getById(query.promotionId)

        promotion.incrementViewCount()
        promotionRepo.update(promotion)

        val author = promotion.authorId?.let { userRepo.getById(it) }
        val saveCount = promotionSaveRepo.countByPromotionId(query.promotionId)
        val isSaved = if (query.requestUserId != null) {
            promotionSaveRepo.existsByUserIdAndPromotionId(query.requestUserId, query.promotionId)
        } else {
            false
        }
        return PromotionMapper.INSTANCE.toPromotionDetail(promotion, author, saveCount, isSaved)
    }

    @Transactional(readOnly = true)
    override fun findPromotionList(
        query: FindPromotionListUseCase.Query,
    ): Pagination<PromotionListItem> = promotionRepo.findPromotionList(
        query.reqUserId,
        query.type,
        query.progressStatus,
        query.regions,
        query.photographyTypes,
        query.sortType,
        query.page,
        query.pageSize,
    )

    @Transactional(readOnly = true)
    override fun findSavedPromotionList(
        query: FindSavedPromotionListUseCase.Query,
    ): Pagination<PromotionListItem> = promotionRepo.findSavedPromotionList(
        requestUserId = query.requestUserId,
        page = query.page,
        pageSize = query.pageSize,
    )

    @Transactional
    override fun postPromotion(command: PostPromotionUseCase.Command): Promotion = promotionRepo.create(
        Promotion.create(
            authorId = command.authorId,
            promotionType = command.promotionType,
            title = command.title,
            content = command.content,
            externalLink = command.externalLink,
            startedAt = command.startedAt,
            endedAt = command.endedAt,
            photographyTypes = command.photographyTypes,
            images = command.images.map { file -> Image(file.name, file.url) },
            activeRegions = command.activeRegions.map { region -> Region(region.category, region.name) }.toSet(),
            hashtags = command.hashtags,
        ),
    )

    @Transactional
    override fun savePromotion(command: SavePromotionUseCase.Command) {
        if (promotionSaveRepo.existsByUserIdAndPromotionId(command.userId, command.promotionId)) {
            throw AlreadyPromotionSaveException()
        }
        promotionSaveRepo.create(PromotionSave.create(command.userId, command.promotionId))
    }

    @Transactional
    override fun updatePromotion(command: UpdatePromotionUseCase.Command): Promotion {
        val promotion = promotionRepo.getById(id = command.promotionId)
        if (promotion.authorId != command.requestUserId) {
            throw PromotionUpdatePermissionDeniedException()
        }
        promotion.update(
            promotionType = command.promotionType,
            title = command.title,
            content = command.content,
            externalLink = command.externalLink,
            startedAt = command.startedAt,
            endedAt = command.endedAt,
            photographyTypes = command.photographyTypes,
            images = command.images,
            activeRegions = command.activeRegions,
            hashtags = command.hashtags,
        )
        return promotionRepo.update(promotion = promotion)
    }

    @Transactional
    override fun deletePromotion(command: DeletePromotionUseCase.Command) {
        val requestUser = command.requestUser
        val promotion = promotionRepo.getById(command.promotionId)
        if (!requestUser.isAdmin && promotion.authorId != requestUser.id) {
            throw PromotionDeletePermissionDeniedException()
        }
        promotionRepo.delete(promotion)
    }

    @Transactional
    override fun unsavePromotion(command: UnsavePromotionUseCase.Command) {
        val promotionSave = promotionSaveRepo.getByUserIdAndPromotionId(command.userId, command.promotionId)
        promotionSaveRepo.delete(promotionSave)
    }
}
