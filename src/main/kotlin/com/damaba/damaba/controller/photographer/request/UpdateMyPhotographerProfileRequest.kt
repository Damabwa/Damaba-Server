package com.damaba.damaba.controller.photographer.request

import com.damaba.damaba.application.photographer.dto.UpdatePhotographerProfileCommand
import com.damaba.damaba.controller.common.request.ImageRequest
import com.damaba.damaba.controller.region.request.RegionRequest
import com.damaba.damaba.domain.common.constant.PhotographyType
import com.damaba.damaba.mapper.ImageMapper
import com.damaba.damaba.mapper.RegionMapper
import io.swagger.v3.oas.annotations.media.Schema

data class UpdateMyPhotographerProfileRequest(
    @Schema(description = "닉네임", example = "치와와")
    val nickname: String,

    @Schema(description = "프로필 이미지")
    val profileImage: ImageRequest?,

    @Schema(description = "주요 촬영 종류 목록")
    val mainPhotographyTypes: Set<PhotographyType>,

    @Schema(description = "활동 지역 목록")
    val activeRegions: Set<RegionRequest>,
) {
    fun toCommand(reqUserId: Long) = UpdatePhotographerProfileCommand(
        photographerId = reqUserId,
        nickname = this.nickname,
        profileImage = this.profileImage?.let { ImageMapper.INSTANCE.toImage(it) },
        mainPhotographyTypes = this.mainPhotographyTypes,
        activeRegions = this.activeRegions.map { RegionMapper.INSTANCE.toRegion(it) }.toSet(),
    )
}
