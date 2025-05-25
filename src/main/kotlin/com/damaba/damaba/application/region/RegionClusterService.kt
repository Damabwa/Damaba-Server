package com.damaba.damaba.application.region

import com.damaba.damaba.application.port.inbound.region.FindRegionClustersUseCase
import com.damaba.damaba.domain.region.RegionCluster
import com.damaba.damaba.infrastructure.region.RegionClusterRepository
import org.springframework.stereotype.Service

@Service
class RegionClusterService(
    private val regionClusterRepo: RegionClusterRepository,
) : FindRegionClustersUseCase {
    override fun findRegionClusters(): List<RegionCluster> = regionClusterRepo.findRegionClusters()
}
