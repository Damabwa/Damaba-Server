package com.damaba.damaba.application.port.outbound.promotion

import com.damaba.damaba.domain.promotion.Promotion

interface CreatePromotionPort {
    /**
     * Promotion을 저장한다.
     *
     * @param promotion 저장할 promotion
     * @return 저장된 promotion
     */
    fun create(promotion: Promotion): Promotion
}
