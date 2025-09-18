package com.damaba.damaba.mapper

import com.damaba.damaba.application.photographer.UpdatePhotographerPageCommand
import com.damaba.damaba.application.photographer.UpdatePhotographerProfileCommand
import com.damaba.damaba.controller.photographer.PhotographerResponse
import com.damaba.damaba.controller.region.RegionResponse
import com.damaba.damaba.domain.photographer.Photographer
import com.damaba.damaba.domain.photographer.PhotographerListItem
import com.damaba.damaba.domain.photographer.PhotographerListItemResponse
import com.damaba.damaba.domain.photographer.PhotographerPage
import com.damaba.damaba.domain.photographer.PhotographerProfile
import com.damaba.damaba.domain.region.Region
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Named
import org.mapstruct.factory.Mappers

@Mapper(uses = [AddressMapper::class, ImageMapper::class, RegionMapper::class])
interface PhotographerMapper {
    @Mapping(
        target = "mainPhotographyTypes",
        expression = "java(new java.util.LinkedHashSet<>(photographer.getMainPhotographyTypes()))",
    )
    @Mapping(target = "activeRegions", source = "activeRegions", qualifiedByName = ["regionsToLinkedHashSet"])
    fun toPhotographerResponse(photographer: Photographer): PhotographerResponse

    @Mapping(source = "saved", target = "isSaved")
    fun toPhotographerListItemResponse(photographerListItem: PhotographerListItem): PhotographerListItemResponse

    fun toPhotographerProfile(command: UpdatePhotographerProfileCommand): PhotographerProfile

    fun toPhotographerPage(command: UpdatePhotographerPageCommand): PhotographerPage

    fun toPhotographerListItem(photographer: Photographer, isSaved: Boolean): PhotographerListItem

    @Named("regionsToLinkedHashSet")
    fun regionsToLinkedHashSet(regions: Set<Region>): LinkedHashSet<RegionResponse> = regions.stream()
        .map { region -> RegionMapper.INSTANCE.toRegionResponse(region) }
        .collect(java.util.stream.Collectors.toCollection { LinkedHashSet() })

    companion object {
        val INSTANCE: PhotographerMapper = Mappers.getMapper(PhotographerMapper::class.java)
    }
}
