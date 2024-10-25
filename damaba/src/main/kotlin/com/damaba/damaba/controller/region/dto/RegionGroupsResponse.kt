package com.damaba.damaba.controller.region.dto

import com.damaba.damaba.domain.region.RegionGroup
import io.swagger.v3.oas.annotations.media.Schema

data class RegionGroupsResponse(
    @Schema(description = "Region group list")
    val regionGroups: List<RegionGroupResponse>,
) {
    companion object {
        fun from(regionsGroups: List<RegionGroup>) =
            RegionGroupsResponse(regionsGroups.map { regionGroup -> RegionGroupResponse.from(regionGroup) })
    }
}
