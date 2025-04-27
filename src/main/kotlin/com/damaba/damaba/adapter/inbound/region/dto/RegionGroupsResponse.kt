package com.damaba.damaba.adapter.inbound.region.dto

import io.swagger.v3.oas.annotations.media.Schema

data class RegionGroupsResponse(
    @Schema(description = "Region group list")
    val regionGroups: List<RegionGroupResponse>,
)
