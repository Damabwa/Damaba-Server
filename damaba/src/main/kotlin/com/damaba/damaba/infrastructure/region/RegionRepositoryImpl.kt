package com.damaba.damaba.infrastructure.region

import com.damaba.damaba.domain.region.RegionGroup
import com.damaba.damaba.domain.region.RegionRepository
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Repository

@Repository
class RegionRepositoryImpl(
    private val resourceLoader: ResourceLoader,
    private val mapper: ObjectMapper,
) : RegionRepository {
    companion object {
        private const val REGION_DATA_FILE_PATH = "classpath:regions.json"
    }

    private val regionGroups: List<RegionGroup> by lazy {
        val regionDataFile = resourceLoader.getResource(REGION_DATA_FILE_PATH)
        val regionMap: Map<String, List<String>> =
            mapper.readValue(regionDataFile.inputStream, object : TypeReference<Map<String, List<String>>>() {})
        regionMap.map { (category, regionNames) -> RegionGroup(category, regionNames) }
    }

    override fun findRegionGroups(): List<RegionGroup> = regionGroups
}
