package com.damaba.damaba.application.region

import com.damaba.damaba.application.port.inbound.region.FindRegionCategoriesUseCase
import com.damaba.damaba.application.port.inbound.region.FindRegionGroupsUseCase
import com.damaba.damaba.domain.region.RegionGroup
import com.damaba.damaba.infrastructure.region.RegionRepository
import org.springframework.stereotype.Service

@Service
class RegionService(
    private val regionRepo: RegionRepository,
) : FindRegionCategoriesUseCase,
    FindRegionGroupsUseCase {
    override fun findRegionCategories(): List<String> = regionRepo.findRegionCategories()

    override fun findRegionGroups(): List<RegionGroup> = regionRepo.findRegionGroups()
}
