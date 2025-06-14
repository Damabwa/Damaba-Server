package com.damaba.damaba.controller.region.dto

import io.swagger.v3.oas.annotations.media.Schema

data class RegionClustersResponse(
    @Schema(description = "List of region clusters")
    val regionClusters: List<RegionClusterResponse>,
)
