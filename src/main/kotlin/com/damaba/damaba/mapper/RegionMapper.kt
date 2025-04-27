package com.damaba.damaba.mapper

import com.damaba.damaba.adapter.inbound.region.dto.RegionClusterResponse
import com.damaba.damaba.adapter.inbound.region.dto.RegionGroupResponse
import com.damaba.damaba.adapter.inbound.region.dto.RegionRequest
import com.damaba.damaba.adapter.inbound.region.dto.RegionResponse
import com.damaba.damaba.domain.region.Region
import com.damaba.damaba.domain.region.RegionCluster
import com.damaba.damaba.domain.region.RegionGroup
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

@Mapper
interface RegionMapper {
    fun toRegionResponse(region: Region): RegionResponse

    fun toRegionGroupResponse(regionGroup: RegionGroup): RegionGroupResponse

    fun toRegionClusterResponse(regionCluster: RegionCluster): RegionClusterResponse

    fun toRegion(regionRequest: RegionRequest): Region

    companion object {
        val INSTANCE: RegionMapper = Mappers.getMapper(RegionMapper::class.java)
    }
}
