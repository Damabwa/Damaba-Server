package com.damaba.damaba.controller.region.response

import io.swagger.v3.oas.annotations.media.Schema

data class RegionResponse(
    @Schema(description = "지역 카테고리", example = "서울")
    val category: String,

    @Schema(description = "지역 이름", example = "강남구")
    val name: String,
)
