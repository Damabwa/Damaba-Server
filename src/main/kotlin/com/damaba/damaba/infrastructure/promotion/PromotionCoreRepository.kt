package com.damaba.damaba.infrastructure.promotion

import com.damaba.damaba.domain.common.Pagination
import com.damaba.damaba.domain.common.constant.PhotographyType
import com.damaba.damaba.domain.promotion.Promotion
import com.damaba.damaba.domain.promotion.PromotionListItem
import com.damaba.damaba.domain.promotion.constant.PromotionProgressStatus
import com.damaba.damaba.domain.promotion.constant.PromotionSortType
import com.damaba.damaba.domain.promotion.constant.PromotionType
import com.damaba.damaba.domain.promotion.exception.PromotionNotFoundException
import com.damaba.damaba.domain.region.RegionFilterCondition
import com.damaba.damaba.infrastructure.common.toPagination
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Repository

@Repository
class PromotionCoreRepository(
    private val promotionJpaRepository: PromotionJpaRepository,
    private val promotionJdslRepository: PromotionJdslRepository,
) : PromotionRepository {
    override fun getById(id: Long): Promotion = getJpaEntityById(id).toPromotion()

    override fun findPromotionList(
        requestUserId: Long?,
        type: PromotionType?,
        progressStatus: PromotionProgressStatus?,
        regions: Set<RegionFilterCondition>,
        photographyTypes: Set<PhotographyType>,
        sortType: PromotionSortType,
        page: Int,
        pageSize: Int,
    ): Pagination<PromotionListItem> = promotionJdslRepository.findPromotionList(
        requestUserId = requestUserId,
        type = type,
        progressStatus = progressStatus,
        regions = regions,
        photographyTypes = photographyTypes,
        sortType = sortType,
        pageable = PageRequest.of(page, pageSize),
    ).toPagination()

    override fun findSavedPromotionList(
        requestUserId: Long,
        page: Int,
        pageSize: Int,
    ): Pagination<PromotionListItem> = promotionJdslRepository.findSavedPromotionList(
        requestUserId = requestUserId,
        pageable = PageRequest.of(page, pageSize),
    ).toPagination()

    override fun create(promotion: Promotion): Promotion {
        val promotionJpaEntity = promotionJpaRepository.save(PromotionJpaEntity.from(promotion))
        return promotionJpaEntity.toPromotion()
    }

    override fun update(promotion: Promotion): Promotion {
        val promotionJpaEntity = getJpaEntityById(promotion.id)
        promotionJpaEntity.update(promotion)
        return promotionJpaEntity.toPromotion()
    }

    override fun delete(promotion: Promotion) {
        val promotionJpaEntity = getJpaEntityById(promotion.id)
        promotionJpaEntity.delete()
    }

    private fun getJpaEntityById(id: Long): PromotionJpaEntity = promotionJpaRepository.findById(id).orElseThrow { PromotionNotFoundException() }
}
