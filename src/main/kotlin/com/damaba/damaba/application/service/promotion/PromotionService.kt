package com.damaba.damaba.application.service.promotion

import com.damaba.damaba.application.port.inbound.promotion.DeletePromotionUseCase
import com.damaba.damaba.application.port.inbound.promotion.FindPromotionListUseCase
import com.damaba.damaba.application.port.inbound.promotion.FindSavedPromotionListUseCase
import com.damaba.damaba.application.port.inbound.promotion.GetPromotionDetailUseCase
import com.damaba.damaba.application.port.inbound.promotion.GetPromotionUseCase
import com.damaba.damaba.application.port.inbound.promotion.PostPromotionUseCase
import com.damaba.damaba.application.port.inbound.promotion.SavePromotionUseCase
import com.damaba.damaba.application.port.inbound.promotion.UnsavePromotionUseCase
import com.damaba.damaba.application.port.inbound.promotion.UpdatePromotionUseCase
import com.damaba.damaba.application.port.outbound.promotion.CountPromotionSavePort
import com.damaba.damaba.application.port.outbound.promotion.CreatePromotionPort
import com.damaba.damaba.application.port.outbound.promotion.CreatePromotionSavePort
import com.damaba.damaba.application.port.outbound.promotion.DeletePromotionPort
import com.damaba.damaba.application.port.outbound.promotion.DeletePromotionSavePort
import com.damaba.damaba.application.port.outbound.promotion.ExistsPromotionSavePort
import com.damaba.damaba.application.port.outbound.promotion.FindPromotionPort
import com.damaba.damaba.application.port.outbound.promotion.GetPromotionPort
import com.damaba.damaba.application.port.outbound.promotion.GetPromotionSavePort
import com.damaba.damaba.application.port.outbound.promotion.UpdatePromotionPort
import com.damaba.damaba.application.port.outbound.user.GetUserPort
import com.damaba.damaba.domain.common.LockType.PESSIMISTIC
import com.damaba.damaba.domain.common.Pagination
import com.damaba.damaba.domain.common.TransactionalLock
import com.damaba.damaba.domain.file.Image
import com.damaba.damaba.domain.promotion.Promotion
import com.damaba.damaba.domain.promotion.PromotionDetail
import com.damaba.damaba.domain.promotion.PromotionListItem
import com.damaba.damaba.domain.promotion.PromotionSave
import com.damaba.damaba.domain.promotion.exception.AlreadyPromotionSaveException
import com.damaba.damaba.domain.promotion.exception.PromotionDeletePermissionDeniedException
import com.damaba.damaba.domain.promotion.exception.PromotionUpdatePermissionDeniedException
import com.damaba.damaba.domain.region.Region
import com.damaba.damaba.mapper.PromotionMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PromotionService(
    private val getUserPort: GetUserPort,

    private val getPromotionPort: GetPromotionPort,
    private val findPromotionPort: FindPromotionPort,
    private val createPromotionPort: CreatePromotionPort,
    private val updatePromotionPort: UpdatePromotionPort,
    private val deletePromotionPort: DeletePromotionPort,

    private val getPromotionSavePort: GetPromotionSavePort,
    private val existsPromotionSavePort: ExistsPromotionSavePort,
    private val countPromotionSavePort: CountPromotionSavePort,
    private val createPromotionSavePort: CreatePromotionSavePort,
    private val deletePromotionSavePort: DeletePromotionSavePort,
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
    override fun getPromotion(promotionId: Long): Promotion = getPromotionPort.getById(promotionId)

    @TransactionalLock(lockType = PESSIMISTIC, domainType = Promotion::class, idFieldName = "query.promotionId")
    override fun getPromotionDetail(query: GetPromotionDetailUseCase.Query): PromotionDetail {
        val promotion = getPromotionPort.getById(query.promotionId)

        promotion.incrementViewCount()
        updatePromotionPort.update(promotion)

        val author = promotion.authorId?.let { getUserPort.getById(it) }
        val saveCount = countPromotionSavePort.countByPromotionId(query.promotionId)
        val isSaved = if (query.requestUserId != null) {
            existsPromotionSavePort.existsByUserIdAndPromotionId(query.requestUserId, query.promotionId)
        } else {
            false
        }
        return PromotionMapper.INSTANCE.toPromotionDetail(promotion, author, saveCount, isSaved)
    }

    @Transactional(readOnly = true)
    override fun findPromotionList(
        query: FindPromotionListUseCase.Query,
    ): Pagination<PromotionListItem> = findPromotionPort.findPromotionList(
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
    ): Pagination<PromotionListItem> = findPromotionPort.findSavedPromotionList(
        requestUserId = query.requestUserId,
        page = query.page,
        pageSize = query.pageSize,
    )

    @Transactional
    override fun postPromotion(command: PostPromotionUseCase.Command): Promotion = createPromotionPort.create(
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
        if (existsPromotionSavePort.existsByUserIdAndPromotionId(command.userId, command.promotionId)) {
            throw AlreadyPromotionSaveException()
        }
        createPromotionSavePort.create(PromotionSave.create(command.userId, command.promotionId))
    }

    @Transactional
    override fun updatePromotion(command: UpdatePromotionUseCase.Command): Promotion {
        val promotion = getPromotionPort.getById(id = command.promotionId)
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
        return updatePromotionPort.update(promotion = promotion)
    }

    @Transactional
    override fun deletePromotion(command: DeletePromotionUseCase.Command) {
        val requestUser = command.requestUser
        val promotion = getPromotionPort.getById(command.promotionId)
        if (!requestUser.isAdmin && promotion.authorId != requestUser.id) {
            throw PromotionDeletePermissionDeniedException()
        }
        deletePromotionPort.delete(promotion)
    }

    @Transactional
    override fun unsavePromotion(command: UnsavePromotionUseCase.Command) {
        val promotionSave = getPromotionSavePort.getByUserIdAndPromotionId(command.userId, command.promotionId)
        deletePromotionSavePort.delete(promotionSave)
    }
}
