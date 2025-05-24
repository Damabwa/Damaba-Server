package com.damaba.damaba.infrastructure.region

import com.damaba.damaba.domain.region.RegionGroup

interface RegionRepository {
    fun findRegionGroups(): List<RegionGroup>
    fun findRegionCategories(): List<String>
}
