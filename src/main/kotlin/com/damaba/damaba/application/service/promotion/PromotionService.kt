package com.damaba.damaba.application.service.promotion

import com.damaba.damaba.application.port.inbound.promotion.FindPromotionListUseCase
import com.damaba.damaba.application.port.inbound.promotion.FindSavedPromotionListUseCase
import com.damaba.damaba.application.port.inbound.promotion.GetPromotionDetailUseCase
import com.damaba.damaba.application.port.inbound.promotion.GetPromotionUseCase
import com.damaba.damaba.application.port.inbound.promotion.PostPromotionUseCase
import com.damaba.damaba.application.port.inbound.promotion.SavePromotionUseCase
import com.damaba.damaba.application.port.inbound.promotion.UnsavePromotionUseCase
import com.damaba.damaba.application.port.outbound.promotion.CountSavedPromotionPort
import com.damaba.damaba.application.port.outbound.promotion.CreatePromotionPort
import com.damaba.damaba.application.port.outbound.promotion.CreateSavedPromotionPort
import com.damaba.damaba.application.port.outbound.promotion.DeleteSavedPromotionPort
import com.damaba.damaba.application.port.outbound.promotion.ExistsSavedPromotionPort
import com.damaba.damaba.application.port.outbound.promotion.FindPromotionPort
import com.damaba.damaba.application.port.outbound.promotion.GetPromotionPort
import com.damaba.damaba.application.port.outbound.promotion.GetSavedPromotionPort
import com.damaba.damaba.application.port.outbound.promotion.UpdatePromotionPort
import com.damaba.damaba.application.port.outbound.user.GetUserPort
import com.damaba.damaba.domain.common.LockType.PESSIMISTIC
import com.damaba.damaba.domain.common.Pagination
import com.damaba.damaba.domain.common.TransactionalLock
import com.damaba.damaba.domain.file.Image
import com.damaba.damaba.domain.promotion.Promotion
import com.damaba.damaba.domain.promotion.PromotionDetail
import com.damaba.damaba.domain.promotion.PromotionListItem
import com.damaba.damaba.domain.promotion.SavedPromotion
import com.damaba.damaba.domain.promotion.exception.AlreadySavedPromotionException
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

    private val getSavedPromotionPort: GetSavedPromotionPort,
    private val existsSavedPromotionPort: ExistsSavedPromotionPort,
    private val countSavedPromotionPort: CountSavedPromotionPort,
    private val createSavedPromotionPort: CreateSavedPromotionPort,
    private val deleteSavedPromotionPort: DeleteSavedPromotionPort,
) : GetPromotionUseCase,
    GetPromotionDetailUseCase,
    FindPromotionListUseCase,
    FindSavedPromotionListUseCase,
    PostPromotionUseCase,
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
        val saveCount = countSavedPromotionPort.countByPromotionId(query.promotionId)
        val isSaved = if (query.requestUserId != null) {
            existsSavedPromotionPort.existsByUserIdAndPromotionId(query.requestUserId, query.promotionId)
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
        if (existsSavedPromotionPort.existsByUserIdAndPromotionId(command.userId, command.promotionId)) {
            throw AlreadySavedPromotionException()
        }
        createSavedPromotionPort.create(SavedPromotion.create(command.userId, command.promotionId))
    }

    @Transactional
    override fun unsavePromotion(command: UnsavePromotionUseCase.Command) {
        val savedPromotion = getSavedPromotionPort.getByUserIdAndPromotionId(command.userId, command.promotionId)
        deleteSavedPromotionPort.delete(savedPromotion)
    }
}
