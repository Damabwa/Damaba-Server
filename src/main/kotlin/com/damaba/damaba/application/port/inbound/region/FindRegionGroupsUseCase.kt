package com.damaba.damaba.application.port.inbound.region

import com.damaba.damaba.domain.region.RegionGroup

interface FindRegionGroupsUseCase {
    fun findRegionGroups(): List<RegionGroup>
}
