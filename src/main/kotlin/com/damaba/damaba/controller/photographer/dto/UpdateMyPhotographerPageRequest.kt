package com.damaba.damaba.controller.photographer.dto

import com.damaba.damaba.application.photographer.UpdatePhotographerPageCommand
import com.damaba.damaba.controller.common.dto.AddressRequest
import com.damaba.damaba.controller.common.dto.ImageRequest
import com.damaba.damaba.mapper.AddressMapper
import com.damaba.damaba.mapper.ImageMapper

data class UpdateMyPhotographerPageRequest(
    val portfolio: List<ImageRequest>,
    val address: AddressRequest?,
    val instagramId: String?,
    val contactLink: String?,
    val description: String,
) {
    fun toCommand(photographerId: Long) = UpdatePhotographerPageCommand(
        photographerId = photographerId,
        portfolio = this.portfolio.map { ImageMapper.INSTANCE.toImage(it) },
        address = this.address?.let { AddressMapper.INSTANCE.toAddress(it) },
        instagramId = this.instagramId,
        contactLink = this.contactLink,
        description = this.description,
    )
}
