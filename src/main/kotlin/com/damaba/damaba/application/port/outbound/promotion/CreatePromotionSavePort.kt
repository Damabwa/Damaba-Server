package com.damaba.damaba.application.port.outbound.promotion

import com.damaba.damaba.domain.promotion.PromotionSave

interface CreatePromotionSavePort {
    fun create(promotionSave: PromotionSave)
}
