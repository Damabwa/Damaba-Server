package com.damaba.damaba.adapter.inbound.region

import com.damaba.damaba.adapter.inbound.region.dto.RegionClustersResponse
import com.damaba.damaba.application.port.inbound.region.FindRegionClustersUseCase
import com.damaba.damaba.mapper.RegionMapper
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Region Cluster 관련 API", description = "일부 기능(필터 등)에서 사용하기 위한 목적으로 군집화된 지역 목록(Region Cluster)에 대한 API입니다.")
@RestController
class RegionClusterController(
    private val findRegionClustersUseCase: FindRegionClustersUseCase,
) {
    @GetMapping("/api/v1/region-clusters")
    fun findRegionClusters(): RegionClustersResponse {
        val regionClusters =
            findRegionClustersUseCase
                .findRegionClusters()
                .map { RegionMapper.INSTANCE.toRegionClusterResponse(it) }
        return RegionClustersResponse(regionClusters)
    }
}
