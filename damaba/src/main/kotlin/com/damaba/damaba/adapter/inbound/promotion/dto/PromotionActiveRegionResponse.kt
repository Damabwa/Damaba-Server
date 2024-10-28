package com.damaba.damaba.adapter.inbound.promotion.dto

import com.damaba.damaba.domain.promotion.PromotionActiveRegion
import io.swagger.v3.oas.annotations.media.Schema

data class PromotionActiveRegionResponse(
    @Schema(description = "지역 카테고리", example = "서울")
    val category: String,

    @Schema(description = "지역 이름", example = "강남구")
    val name: String,
) {
    companion object {
        fun from(promotionActiveRegion: PromotionActiveRegion) = PromotionActiveRegionResponse(
            category = promotionActiveRegion.category,
            name = promotionActiveRegion.name,
        )
    }
}
