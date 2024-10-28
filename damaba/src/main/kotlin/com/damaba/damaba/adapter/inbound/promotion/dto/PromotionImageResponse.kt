package com.damaba.damaba.adapter.inbound.promotion.dto

import com.damaba.damaba.domain.promotion.PromotionImage
import io.swagger.v3.oas.annotations.media.Schema

data class PromotionImageResponse(
    @Schema(description = "이미지 파일 이름", example = "promotion-image-1")
    val name: String,

    @Schema(description = "이미지 파일 url", example = "https://promotion-image-1")
    val url: String,
) {
    companion object {
        fun from(promotionImage: PromotionImage) = PromotionImageResponse(
            name = promotionImage.name,
            url = promotionImage.url,
        )
    }
}
