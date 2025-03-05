package com.damaba.damaba.mapper

import com.damaba.damaba.adapter.inbound.photographer.dto.PhotographerResponse
import com.damaba.damaba.application.port.inbound.photographer.UpdatePhotographerProfileUseCase
import com.damaba.damaba.domain.photographer.Photographer
import com.damaba.damaba.domain.photographer.PhotographerProfile
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

@Mapper(uses = [AddressMapper::class, ImageMapper::class])
interface PhotographerMapper {
    fun toPhotographerResponse(photographer: Photographer): PhotographerResponse

    fun toPhotographerProfile(command: UpdatePhotographerProfileUseCase.Command): PhotographerProfile

    companion object {
        val INSTANCE: PhotographerMapper = Mappers.getMapper(PhotographerMapper::class.java)
    }
}
