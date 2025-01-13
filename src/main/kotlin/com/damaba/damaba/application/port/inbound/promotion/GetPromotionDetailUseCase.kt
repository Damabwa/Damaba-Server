package com.damaba.damaba.application.port.inbound.promotion

import com.damaba.damaba.domain.promotion.PromotionDetail

interface GetPromotionDetailUseCase {
    /**
     * 프로모션 상세 정보(`PromotionDetail`)을 조회한다.
     * 상세 조회 시, 조회수가 1 증가한다.
     *
     * @param promotionId 조회할 프로모션 id
     * @return 프로모션 상세 정보
     */
    fun getPromotionDetail(promotionId: Long): PromotionDetail
}
