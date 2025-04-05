package com.damaba.damaba.application.port.outbound.promotion

import com.damaba.damaba.domain.promotion.Promotion

interface DeletePromotionPort {
    fun delete(promotion: Promotion)
}
