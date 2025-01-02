package com.damaba.damaba.application.service.region

import com.damaba.damaba.application.port.inbound.region.FindRegionCategoriesUseCase
import com.damaba.damaba.application.port.inbound.region.FindRegionGroupsUseCase
import com.damaba.damaba.application.port.outbound.region.FindRegionCategoriesPort
import com.damaba.damaba.application.port.outbound.region.FindRegionGroupsPort
import com.damaba.damaba.domain.region.RegionGroup
import org.springframework.stereotype.Service

@Service
class RegionService(
    private val findRegionCategoriesPort: FindRegionCategoriesPort,
    private val findRegionGroupsPort: FindRegionGroupsPort,
) : FindRegionCategoriesUseCase,
    FindRegionGroupsUseCase {
    override fun findRegionCategories(): List<String> =
        findRegionCategoriesPort.findRegionCategories()

    override fun findRegionGroups(): List<RegionGroup> =
        findRegionGroupsPort.findRegionGroups()
}
