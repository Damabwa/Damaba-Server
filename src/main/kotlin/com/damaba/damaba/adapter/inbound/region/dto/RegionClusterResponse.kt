package com.damaba.damaba.adapter.inbound.region.dto

import io.swagger.v3.oas.annotations.media.Schema

class RegionClusterResponse(
    @Schema(description = "군집화된 시/도 단위 지역의 상위 카테고리", example = "서울")
    val category: String,

    @Schema(description = "군집화된 시/군/구 단위의 지역 리스트 ", example = "서대문/은평")
    val clusters: List<String>,
)
