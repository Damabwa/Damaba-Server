package com.damaba.damaba.application.service.promotion

import com.damaba.damaba.application.port.inbound.promotion.FindPromotionsUseCase
import com.damaba.damaba.application.port.inbound.promotion.GetPromotionDetailUseCase
import com.damaba.damaba.application.port.inbound.promotion.PostPromotionUseCase
import com.damaba.damaba.application.port.outbound.promotion.FindPromotionsPort
import com.damaba.damaba.application.port.outbound.promotion.GetPromotionPort
import com.damaba.damaba.application.port.outbound.promotion.SavePromotionPort
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
    private val savePromotionPort: SavePromotionPort,
) : GetPromotionDetailUseCase,
    FindPromotionsUseCase,
    PostPromotionUseCase {

    @Transactional(readOnly = true)
    override fun getPromotionDetail(promotionId: Long): Promotion = getPromotionPort.getById(promotionId)

    @Transactional(readOnly = true)
    override fun findPromotions(query: FindPromotionsUseCase.Query): Pagination<Promotion> = findPromotionsPort.findPromotions(query.page, query.pageSize)

    @Transactional
    override fun postPromotion(command: PostPromotionUseCase.Command): Promotion = savePromotionPort.save(
        Promotion.create(
            authorId = command.authorId,
            type = command.type,
            eventType = command.eventType,
            title = command.title,
            content = command.content,
            address = command.address,
            externalLink = command.externalLink,
            startedAt = command.startedAt,
            endedAt = command.endedAt,
            photographerName = command.photographerName,
            photographerInstagramId = command.photographerInstagramId,
            images = command.images.map { file -> Image(file.name, file.url) },
            activeRegions = command.activeRegions.map { region -> Region(region.category, region.name) }.toSet(),
            hashtags = command.hashtags,
        ),
    )
}
