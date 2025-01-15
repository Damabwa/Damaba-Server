package com.damaba.damaba.adapter.outbound.promotion

import com.damaba.damaba.adapter.outbound.common.toPagination
import com.damaba.damaba.application.port.outbound.promotion.CreatePromotionPort
import com.damaba.damaba.application.port.outbound.promotion.FindPromotionsPort
import com.damaba.damaba.application.port.outbound.promotion.GetPromotionPort
import com.damaba.damaba.application.port.outbound.promotion.UpdatePromotionPort
import com.damaba.damaba.domain.common.Pagination
import com.damaba.damaba.domain.common.PhotographyType
import com.damaba.damaba.domain.promotion.Promotion
import com.damaba.damaba.domain.promotion.constant.PromotionProgressStatus
import com.damaba.damaba.domain.promotion.constant.PromotionSortType
import com.damaba.damaba.domain.promotion.constant.PromotionType
import com.damaba.damaba.domain.promotion.exception.PromotionNotFoundException
import com.damaba.damaba.domain.region.RegionFilterCondition
import com.damaba.damaba.mapper.PromotionMapper
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Repository

@Repository
class PromotionCoreRepository(
    private val promotionJpaRepository: PromotionJpaRepository,
    private val promotionJdslRepository: PromotionJdslRepository,
) : GetPromotionPort,
    FindPromotionsPort,
    CreatePromotionPort,
    UpdatePromotionPort {
    override fun getById(id: Long): Promotion = PromotionMapper.INSTANCE.toPromotion(getJpaEntityById(id))

    override fun findPromotions(
        type: PromotionType?,
        progressStatus: PromotionProgressStatus?,
        regions: Set<RegionFilterCondition>,
        photographyTypes: Set<PhotographyType>,
        sortType: PromotionSortType,
        page: Int,
        pageSize: Int,
    ): Pagination<Promotion> = promotionJdslRepository
        .findPromotions(type, progressStatus, regions, photographyTypes, sortType, PageRequest.of(page, pageSize))
        .toPagination { PromotionMapper.INSTANCE.toPromotion(it) }

    override fun create(promotion: Promotion): Promotion {
        val promotionJpaEntity = promotionJpaRepository.save(PromotionMapper.INSTANCE.toPromotionJpaEntity(promotion))
        return PromotionMapper.INSTANCE.toPromotion(promotionJpaEntity)
    }

    override fun update(promotion: Promotion): Promotion {
        val promotionJpaEntity = getJpaEntityById(promotion.id)
        promotionJpaEntity.update(promotion)
        return PromotionMapper.INSTANCE.toPromotion(promotionJpaEntity)
    }

    private fun getJpaEntityById(id: Long): PromotionJpaEntity = promotionJpaRepository.findById(id).orElseThrow { PromotionNotFoundException() }
}
