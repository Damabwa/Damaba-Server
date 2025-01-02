package com.damaba.damaba.adapter.inbound.photographer.dto

import com.damaba.damaba.adapter.inbound.common.dto.ImageRequest
import com.damaba.damaba.adapter.inbound.region.dto.RegionRequest
import com.damaba.damaba.application.port.inbound.photographer.RegisterPhotographerUseCase
import com.damaba.damaba.domain.common.PhotographyType
import com.damaba.damaba.mapper.ImageMapper
import com.damaba.damaba.mapper.RegionMapper
import com.damaba.user.domain.user.constant.Gender

data class RegisterPhotographerRequest(
    val nickname: String,
    val gender: Gender,
    val instagramId: String?,
    val profileImage: ImageRequest,
    val mainPhotographyTypes: Set<PhotographyType>,
    val activeRegions: Set<RegionRequest>,
) {
    fun toCommand(requesterId: Long) = RegisterPhotographerUseCase.Command(
        userId = requesterId,
        nickname = nickname,
        gender = gender,
        instagramId = instagramId,
        profileImage = ImageMapper.INSTANCE.toImage(profileImage),
        mainPhotographyTypes = mainPhotographyTypes,
        activeRegions = activeRegions.map { regionRequest -> RegionMapper.INSTANCE.toRegion(regionRequest) }.toSet(),
    )
}
