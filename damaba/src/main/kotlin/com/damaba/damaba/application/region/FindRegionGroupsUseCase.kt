package com.damaba.damaba.application.region

import com.damaba.damaba.domain.region.RegionGroup
import com.damaba.damaba.domain.region.RegionService
import org.springframework.stereotype.Service

@Service
class FindRegionGroupsUseCase(private val regionService: RegionService) {
    operator fun invoke(): List<RegionGroup> = regionService.findRegionGroups()
}
