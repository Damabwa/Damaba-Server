package com.damaba.damaba.domain.region

interface RegionRepository {
    fun findRegionGroups(): List<RegionGroup>
}
