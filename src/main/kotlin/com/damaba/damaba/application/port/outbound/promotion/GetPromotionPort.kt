package com.damaba.damaba.application.port.outbound.promotion

import com.damaba.damaba.domain.promotion.Promotion

interface GetPromotionPort {
    /**
     * Promotion을 단건 조회한다.
     *
     * @param id 조회할 promotion의 id
     * @return 조회된 promotion
     */
    fun getById(id: Long): Promotion
}
