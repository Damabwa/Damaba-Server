package com.damaba.damaba.controller.region

import com.damaba.damaba.application.region.RegionService
import com.damaba.damaba.controller.region.dto.RegionCategoriesResponse
import com.damaba.damaba.controller.region.dto.RegionGroupsResponse
import com.damaba.damaba.mapper.RegionMapper
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Region(지역) 관련 API", description = "담아봐 서비스 내에서 다루는 지역에 대한 API입니다.")
@RestController
class RegionController(private val regionService: RegionService) {
    @GetMapping("/api/v1/regions/categories")
    fun findRegionCategories(): RegionCategoriesResponse {
        val categories = regionService.findRegionCategories()
        return RegionCategoriesResponse(categories)
    }

    @Operation(
        summary = "Region group 리스트 조회",
        description = "<p>Region group 리스트를 조회합니다." +
            "<p>Region group이란 region의 카테고리와 해당 카테고리에 대한 지역 리스트가 포함된 데이터셋을 의미합니다.",
    )
    @GetMapping("/api/v1/regions/groups")
    fun findRegionGroupsV1(): RegionGroupsResponse {
        val regionGroupResponses = regionService.findRegionGroups()
            .map { RegionMapper.INSTANCE.toRegionGroupResponse(it) }
        return RegionGroupsResponse(regionGroupResponses)
    }
}
