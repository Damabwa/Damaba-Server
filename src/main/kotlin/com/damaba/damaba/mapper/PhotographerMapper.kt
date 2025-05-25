package com.damaba.damaba.mapper

import com.damaba.damaba.application.photographer.dto.UpdatePhotographerPageCommand
import com.damaba.damaba.application.photographer.dto.UpdatePhotographerProfileCommand
import com.damaba.damaba.controller.photographer.response.PhotographerResponse
import com.damaba.damaba.domain.photographer.Photographer
import com.damaba.damaba.domain.photographer.PhotographerListItem
import com.damaba.damaba.domain.photographer.PhotographerListItemResponse
import com.damaba.damaba.domain.photographer.PhotographerPage
import com.damaba.damaba.domain.photographer.PhotographerProfile
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.factory.Mappers

@Mapper(uses = [AddressMapper::class, ImageMapper::class])
interface PhotographerMapper {
    fun toPhotographerResponse(photographer: Photographer): PhotographerResponse

    @Mapping(source = "saved", target = "isSaved")
    fun toPhotographerListItemResponse(photographerListItem: PhotographerListItem): PhotographerListItemResponse

    fun toPhotographerProfile(command: UpdatePhotographerProfileCommand): PhotographerProfile

    fun toPhotographerPage(command: UpdatePhotographerPageCommand): PhotographerPage

    fun toPhotographerListItem(photographer: Photographer, isSaved: Boolean): PhotographerListItem

    companion object {
        val INSTANCE: PhotographerMapper = Mappers.getMapper(PhotographerMapper::class.java)
    }
}
