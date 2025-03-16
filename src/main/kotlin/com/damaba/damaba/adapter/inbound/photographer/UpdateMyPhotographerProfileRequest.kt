package com.damaba.damaba.adapter.inbound.photographer

import com.damaba.damaba.adapter.inbound.common.dto.ImageRequest
import com.damaba.damaba.adapter.inbound.region.dto.RegionRequest
import com.damaba.damaba.application.port.inbound.photographer.UpdatePhotographerProfileUseCase
import com.damaba.damaba.domain.common.PhotographyType
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
    fun toCommand(reqUserId: Long) = UpdatePhotographerProfileUseCase.Command(
        photographerId = reqUserId,
        nickname = this.nickname,
        profileImage = this.profileImage?.let { ImageMapper.INSTANCE.toImage(it) },
        mainPhotographyTypes = this.mainPhotographyTypes,
        activeRegions = this.activeRegions.map { RegionMapper.INSTANCE.toRegion(it) }.toSet(),
    )
}
