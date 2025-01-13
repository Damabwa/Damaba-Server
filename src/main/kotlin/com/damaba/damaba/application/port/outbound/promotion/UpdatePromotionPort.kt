package com.damaba.damaba.application.port.outbound.promotion

import com.damaba.damaba.domain.promotion.Promotion

interface UpdatePromotionPort {
    /**
     * 프로모션 정보를 수정합니다.
     *
     * @param promotion 수정하고자 하는 정보가 담긴 프로모션
     * @return 수정된 프로모션
     */
    fun update(promotion: Promotion): Promotion
}
