package com.damaba.damaba.application.port.inbound.region

import com.damaba.damaba.domain.region.RegionCluster

interface FindRegionClustersUseCase {
    fun findRegionClusters(): List<RegionCluster>
}
