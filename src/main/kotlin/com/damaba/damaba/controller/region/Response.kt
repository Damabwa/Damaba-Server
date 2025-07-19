package com.damaba.damaba.controller.region

import io.swagger.v3.oas.annotations.media.Schema

data class RegionCategoriesResponse(
    @Schema(description = "지역 카테고리 리스트", example = "[\"서울\", \"경기\"]")
    val categories: List<String>,
)

class RegionClusterResponse(
    @Schema(description = "군집화된 시/도 단위 지역의 상위 카테고리", example = "서울")
    val category: String,

    @Schema(description = "군집화된 시/군/구 단위의 지역 리스트 ", example = "서대문/은평")
    val clusters: List<String>,
)

data class RegionClustersResponse(
    @Schema(description = "List of region clusters")
    val regionClusters: List<RegionClusterResponse>,
)

data class RegionGroupResponse(
    @Schema(description = "Region category", example = "서울")
    val category: String,

    @Schema(description = "Region list", example = "[\"종로구\", \"용산구\"]")
    val regions: List<String>,
)

data class RegionGroupsResponse(
    @Schema(description = "Region group list")
    val regionGroups: List<RegionGroupResponse>,
)

data class RegionResponse(
    @Schema(description = "지역 카테고리", example = "서울")
    val category: String,

    @Schema(description = "지역 이름", example = "강남구")
    val name: String,
)
