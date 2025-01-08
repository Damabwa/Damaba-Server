package com.damaba.damaba.application.port.outbound.region

import com.damaba.damaba.domain.region.RegionCluster

interface FindRegionClustersPort {
    fun findRegionClusters(): List<RegionCluster>
}
