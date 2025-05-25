package com.damaba.damaba.controller.region.dto

import io.swagger.v3.oas.annotations.media.Schema

data class RegionCategoriesResponse(
    @Schema(description = "지역 카테고리 리스트", example = "[\"서울\", \"경기\"]")
    val categories: List<String>,
)
