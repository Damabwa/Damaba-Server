package com.damaba.damaba.application.port.outbound.region

import com.damaba.damaba.domain.region.RegionGroup

interface FindRegionGroupsPort {
    fun findRegionGroups(): List<RegionGroup>
}
