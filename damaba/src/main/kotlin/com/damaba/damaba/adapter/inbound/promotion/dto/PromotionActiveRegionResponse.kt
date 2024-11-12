package com.damaba.damaba.adapter.inbound.promotion.dto

import com.damaba.damaba.domain.region.Region
import io.swagger.v3.oas.annotations.media.Schema

data class PromotionActiveRegionResponse(
    @Schema(description = "지역 카테고리", example = "서울")
    val category: String,

    @Schema(description = "지역 이름", example = "강남구")
    val name: String,
) {
    companion object {
        fun from(region: Region) = PromotionActiveRegionResponse(
            category = region.category,
            name = region.name,
        )
    }
}
