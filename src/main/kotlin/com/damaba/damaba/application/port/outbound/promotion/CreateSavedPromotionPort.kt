package com.damaba.damaba.application.port.outbound.promotion

import com.damaba.damaba.domain.promotion.SavedPromotion

interface CreateSavedPromotionPort {
    fun create(savedPromotion: SavedPromotion)
}
