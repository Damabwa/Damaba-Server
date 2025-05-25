package com.damaba.damaba.application.region

import com.damaba.damaba.domain.region.RegionGroup
import com.damaba.damaba.infrastructure.region.RegionRepository
import org.springframework.stereotype.Service

@Service
class RegionService(
    private val regionRepo: RegionRepository,
) {
    fun findRegionCategories(): List<String> = regionRepo.findRegionCategories()

    fun findRegionGroups(): List<RegionGroup> = regionRepo.findRegionGroups()
}
