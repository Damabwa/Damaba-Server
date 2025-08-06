package com.damaba.damaba.controller.photographer

import com.damaba.damaba.application.photographer.RegisterPhotographerCommand
import com.damaba.damaba.application.photographer.UpdatePhotographerPageCommand
import com.damaba.damaba.application.photographer.UpdatePhotographerProfileCommand
import com.damaba.damaba.application.term.TermItem
import com.damaba.damaba.controller.common.AddressRequest
import com.damaba.damaba.controller.common.ImageRequest
import com.damaba.damaba.controller.region.RegionRequest
import com.damaba.damaba.controller.term.AgreementRequestItem
import com.damaba.damaba.domain.common.constant.PhotographyType
import com.damaba.damaba.domain.term.TermType
import com.damaba.damaba.domain.user.constant.Gender
import com.damaba.damaba.mapper.AddressMapper
import com.damaba.damaba.mapper.ImageMapper
import com.damaba.damaba.mapper.RegionMapper
import io.swagger.v3.oas.annotations.media.Schema

data class RegisterPhotographerRequest(
    val nickname: String,
    val gender: Gender,
    val instagramId: String?,
    val profileImage: ImageRequest,
    val mainPhotographyTypes: Set<PhotographyType>,
    val activeRegions: Set<RegionRequest>,
    val agreements: List<AgreementRequestItem>,
) {
    fun toCommand(requesterId: Long) = RegisterPhotographerCommand(
        userId = requesterId,
        nickname = nickname,
        gender = gender,
        instagramId = instagramId,
        profileImage = ImageMapper.INSTANCE.toImage(profileImage),
        mainPhotographyTypes = mainPhotographyTypes,
        activeRegions = activeRegions.map { regionRequest -> RegionMapper.INSTANCE.toRegion(regionRequest) }.toSet(),
        terms = agreements.map { TermItem(it.type, it.agreed) },
    )
}

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

    data class AgreementRequestItem(
        @Schema(description = "약관 종류", example = "SERVICE_TERMS")
        val type: TermType,

        @Schema(description = "사용자 동의 여부", example = "ture")
        val agreed: Boolean,
    )
}
