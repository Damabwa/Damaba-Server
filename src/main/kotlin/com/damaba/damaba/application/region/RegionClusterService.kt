package com.damaba.damaba.application.region

import com.damaba.damaba.domain.region.RegionCluster
import com.damaba.damaba.infrastructure.region.RegionClusterRepository
import org.springframework.stereotype.Service

@Service
class RegionClusterService(
    private val regionClusterRepo: RegionClusterRepository,
) {
    fun findRegionClusters(): List<RegionCluster> = regionClusterRepo.findRegionClusters()
}
