package com.damaba.damaba.controller.photographer.request

import com.damaba.damaba.application.photographer.dto.RegisterPhotographerCommand
import com.damaba.damaba.controller.common.request.ImageRequest
import com.damaba.damaba.controller.region.request.RegionRequest
import com.damaba.damaba.domain.common.constant.PhotographyType
import com.damaba.damaba.domain.user.constant.Gender
import com.damaba.damaba.mapper.ImageMapper
import com.damaba.damaba.mapper.RegionMapper

data class RegisterPhotographerRequest(
    val nickname: String,
    val gender: Gender,
    val instagramId: String?,
    val profileImage: ImageRequest,
    val mainPhotographyTypes: Set<PhotographyType>,
    val activeRegions: Set<RegionRequest>,
) {
    fun toCommand(requesterId: Long) = RegisterPhotographerCommand(
        userId = requesterId,
        nickname = nickname,
        gender = gender,
        instagramId = instagramId,
        profileImage = ImageMapper.INSTANCE.toImage(profileImage),
        mainPhotographyTypes = mainPhotographyTypes,
        activeRegions = activeRegions.map { regionRequest -> RegionMapper.INSTANCE.toRegion(regionRequest) }.toSet(),
    )
}
