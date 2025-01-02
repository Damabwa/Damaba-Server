package com.damaba.damaba.application.port.outbound.promotion

import com.damaba.damaba.domain.common.Pagination
import com.damaba.damaba.domain.promotion.Promotion

interface FindPromotionsPort {
    fun findPromotions(page: Int, pageSize: Int): Pagination<Promotion>
}
