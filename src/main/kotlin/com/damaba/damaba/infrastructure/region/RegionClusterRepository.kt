package com.damaba.damaba.infrastructure.region

import com.damaba.damaba.domain.region.RegionCluster

interface RegionClusterRepository {
    fun findRegionClusters(): List<RegionCluster>
}
