package com.damaba.damaba.application.service.promotion

import com.damaba.damaba.application.port.inbound.promotion.FindPromotionsUseCase
import com.damaba.damaba.application.port.inbound.promotion.GetPromotionUseCase
import com.damaba.damaba.application.port.inbound.promotion.PostPromotionUseCase
import com.damaba.damaba.application.port.outbound.promotion.CreatePromotionPort
import com.damaba.damaba.application.port.outbound.promotion.FindPromotionsPort
import com.damaba.damaba.application.port.outbound.promotion.GetPromotionPort
import com.damaba.damaba.domain.common.Pagination
import com.damaba.damaba.domain.file.Image
import com.damaba.damaba.domain.promotion.Promotion
import com.damaba.damaba.domain.region.Region
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PromotionService(
    private val getPromotionPort: GetPromotionPort,
    private val findPromotionsPort: FindPromotionsPort,
    private val createPromotionPort: CreatePromotionPort,
) : GetPromotionUseCase,
    FindPromotionsUseCase,
    PostPromotionUseCase {

    @Transactional(readOnly = true)
    override fun getPromotion(promotionId: Long): Promotion = getPromotionPort.getById(promotionId)

    @Transactional(readOnly = true)
    override fun findPromotions(query: FindPromotionsUseCase.Query): Pagination<Promotion> = findPromotionsPort.findPromotions(query.page, query.pageSize)

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
            photographerName = command.photographerName,
            photographerInstagramId = command.photographerInstagramId,
            photographyTypes = command.photographyTypes,
            images = command.images.map { file -> Image(file.name, file.url) },
            activeRegions = command.activeRegions.map { region -> Region(region.category, region.name) }.toSet(),
            hashtags = command.hashtags,
        ),
    )
}
