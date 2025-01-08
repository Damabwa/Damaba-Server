package com.damaba.damaba.application.service.region

import com.damaba.damaba.application.port.inbound.region.FindRegionClustersUseCase
import com.damaba.damaba.application.port.outbound.region.FindRegionClustersPort
import com.damaba.damaba.domain.region.RegionCluster
import org.springframework.stereotype.Service

@Service
class RegionClusterService(
    private val findRegionClustersPort: FindRegionClustersPort,
) : FindRegionClustersUseCase {
    override fun findRegionClusters(): List<RegionCluster> = findRegionClustersPort.findRegionClusters()
}
