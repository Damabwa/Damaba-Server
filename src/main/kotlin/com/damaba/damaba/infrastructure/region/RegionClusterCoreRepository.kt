package com.damaba.damaba.infrastructure.region

import com.damaba.damaba.application.port.outbound.region.FindRegionClustersPort
import com.damaba.damaba.domain.region.RegionCluster
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Repository

@Repository
class RegionClusterCoreRepository(
    private val resourceLoader: ResourceLoader,
    private val mapper: ObjectMapper,
) : FindRegionClustersPort {
    private val regionClusters: List<RegionCluster> by lazy {
        val regionClustersFile = resourceLoader.getResource(REGION_CLUSTERS_DATE_FILE_PATH)
        val regionClusterMap: Map<String, List<String>> =
            mapper.readValue(regionClustersFile.inputStream, object : TypeReference<Map<String, List<String>>>() {})
        regionClusterMap.map { (category, clusters) -> RegionCluster(category, clusters) }
    }

    override fun findRegionClusters(): List<RegionCluster> = regionClusters

    companion object {
        private const val REGION_CLUSTERS_DATE_FILE_PATH = "classpath:region-clusters.json"
    }
}
