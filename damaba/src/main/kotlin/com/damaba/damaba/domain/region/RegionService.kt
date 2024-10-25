package com.damaba.damaba.domain.region

import org.springframework.stereotype.Service

@Service
class RegionService(private val regionRepository: RegionRepository) {
    fun findRegionGroups(): List<RegionGroup> =
        regionRepository.findRegionGroups()
}
