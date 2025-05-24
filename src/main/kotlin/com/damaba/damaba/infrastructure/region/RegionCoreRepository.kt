package com.damaba.damaba.infrastructure.region

import com.damaba.damaba.application.port.outbound.region.FindRegionCategoriesPort
import com.damaba.damaba.application.port.outbound.region.FindRegionGroupsPort
import com.damaba.damaba.domain.region.RegionGroup
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Repository

@Repository
class RegionCoreRepository(
    private val resourceLoader: ResourceLoader,
    private val mapper: ObjectMapper,
) : FindRegionCategoriesPort,
    FindRegionGroupsPort {
    companion object {
        private const val REGION_DATA_FILE_PATH = "classpath:regions.json"
    }

    private val regionGroups: List<RegionGroup> by lazy {
        val regionDataFile = resourceLoader.getResource(REGION_DATA_FILE_PATH)
        val regionMap: Map<String, List<String>> =
            mapper.readValue(regionDataFile.inputStream, object : TypeReference<Map<String, List<String>>>() {})
        regionMap.map { (category, regionNames) -> RegionGroup(category, regionNames) }
    }

    override fun findRegionCategories(): List<String> = regionGroups.map { regionGroup -> regionGroup.category }

    override fun findRegionGroups(): List<RegionGroup> = regionGroups
}
