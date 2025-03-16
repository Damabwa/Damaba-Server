package com.damaba.damaba.application.port.outbound.promotion

import com.damaba.damaba.domain.promotion.PromotionSave

interface DeletePromotionSavePort {
    fun delete(promotionSave: PromotionSave)
}
