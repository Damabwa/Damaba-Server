package com.damaba.damaba.infrastructure.promotion

import com.damaba.damaba.domain.common.Pagination
import com.damaba.damaba.domain.common.constant.PhotographyType
import com.damaba.damaba.domain.promotion.Promotion
import com.damaba.damaba.domain.promotion.PromotionListItem
import com.damaba.damaba.domain.promotion.constant.PromotionProgressStatus
import com.damaba.damaba.domain.promotion.constant.PromotionSortType
import com.damaba.damaba.domain.promotion.constant.PromotionType
import com.damaba.damaba.domain.region.RegionFilterCondition

interface PromotionRepository {
    /**
     * Promotion을 저장한다.
     *
     * @param promotion 저장할 promotion
     * @return 저장된 promotion
     */
    fun create(promotion: Promotion): Promotion

    fun findPromotionsByAuthorId(authorId: Long): List<Promotion>

    fun findPromotionList(
        requestUserId: Long?,
        type: PromotionType?,
        progressStatus: PromotionProgressStatus?,
        regions: Set<RegionFilterCondition>,
        photographyTypes: Set<PhotographyType>,
        searchKeyword: String?,
        sortType: PromotionSortType,
        page: Int,
        pageSize: Int,
    ): Pagination<PromotionListItem>

    fun findSavedPromotionList(
        requestUserId: Long,
        page: Int,
        pageSize: Int,
    ): Pagination<PromotionListItem>

    /**
     * Promotion을 단건 조회한다.
     *
     * @param id 조회할 promotion의 id
     * @return 조회된 promotion
     */
    fun getById(id: Long): Promotion

    /**
     * 프로모션 정보를 수정합니다.
     *
     * @param promotion 수정하고자 하는 정보가 담긴 프로모션
     * @return 수정된 프로모션
     */
    fun update(promotion: Promotion): Promotion

    fun delete(promotion: Promotion)
}
