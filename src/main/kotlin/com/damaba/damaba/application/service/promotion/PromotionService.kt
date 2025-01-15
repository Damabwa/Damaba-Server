package com.damaba.damaba.application.service.promotion

import com.damaba.damaba.application.port.inbound.promotion.FindPromotionsUseCase
import com.damaba.damaba.application.port.inbound.promotion.GetPromotionDetailUseCase
import com.damaba.damaba.application.port.inbound.promotion.GetPromotionUseCase
import com.damaba.damaba.application.port.inbound.promotion.PostPromotionUseCase
import com.damaba.damaba.application.port.outbound.promotion.CreatePromotionPort
import com.damaba.damaba.application.port.outbound.promotion.FindPromotionsPort
import com.damaba.damaba.application.port.outbound.promotion.GetPromotionPort
import com.damaba.damaba.application.port.outbound.promotion.UpdatePromotionPort
import com.damaba.damaba.application.port.outbound.user.GetUserPort
import com.damaba.damaba.domain.common.LockType
import com.damaba.damaba.domain.common.Pagination
import com.damaba.damaba.domain.common.TransactionalLock
import com.damaba.damaba.domain.file.Image
import com.damaba.damaba.domain.promotion.Promotion
import com.damaba.damaba.domain.promotion.PromotionDetail
import com.damaba.damaba.domain.region.Region
import com.damaba.damaba.mapper.PromotionMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PromotionService(
    private val getPromotionPort: GetPromotionPort,
    private val getUserPort: GetUserPort,
    private val findPromotionsPort: FindPromotionsPort,
    private val createPromotionPort: CreatePromotionPort,
    private val updatePromotionPort: UpdatePromotionPort,
) : GetPromotionUseCase,
    GetPromotionDetailUseCase,
    FindPromotionsUseCase,
    PostPromotionUseCase {

    @Transactional(readOnly = true)
    override fun getPromotion(promotionId: Long): Promotion = getPromotionPort.getById(promotionId)

    // TODO: `saveCount`, `isSaved`는 추후 저장 기능 구현 후 반영 로직 추가
    @TransactionalLock(lockType = LockType.PESSIMISTIC, domainType = Promotion::class, idFieldName = "promotionId")
    override fun getPromotionDetail(promotionId: Long): PromotionDetail {
        val promotion = getPromotionPort.getById(promotionId)

        promotion.incrementViewCount()
        updatePromotionPort.update(promotion)

        val author = promotion.authorId?.let { getUserPort.getById(it) }
        val saveCount = 0
        val isSaved = false
        return PromotionMapper.INSTANCE.toPromotionDetail(promotion, author, saveCount, isSaved)
    }

    @Transactional(readOnly = true)
    override fun findPromotions(
        query: FindPromotionsUseCase.Query,
    ): Pagination<Promotion> = findPromotionsPort.findPromotions(
        query.type,
        query.progressStatus,
        query.regions,
        query.photographyTypes,
        query.sortType,
        query.page,
        query.pageSize,
    )

    @Transactional
    override fun postPromotion(command: PostPromotionUseCase.Command): Promotion = createPromotionPort.create(
        Promotion.create(
            authorId = command.authorId,
            promotionType = command.promotionType,
            title = command.title,
            content = command.content,
            address = command.address,
            externalLink = command.externalLink,
            startedAt = command.startedAt,
            endedAt = command.endedAt,
            photographyTypes = command.photographyTypes,
            images = command.images.map { file -> Image(file.name, file.url) },
            activeRegions = command.activeRegions.map { region -> Region(region.category, region.name) }.toSet(),
            hashtags = command.hashtags,
        ),
    )
}
