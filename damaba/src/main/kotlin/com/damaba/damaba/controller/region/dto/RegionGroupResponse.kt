package com.damaba.damaba.controller.region.dto

import com.damaba.damaba.domain.region.RegionGroup
import io.swagger.v3.oas.annotations.media.Schema

data class RegionGroupResponse(
    @Schema(description = "Region category", example = "서울")
    val category: String,

    @Schema(description = "Region list", example = "[\"종로구\", \"용산구\"]")
    val regions: List<String>,
) {
    companion object {
        fun from(regionGroup: RegionGroup): RegionGroupResponse = RegionGroupResponse(
            category = regionGroup.category,
            regions = regionGroup.regions,
        )
    }
}
