package com.damaba.damaba.adapter.inbound.user.dto

import com.damaba.damaba.adapter.inbound.common.dto.ImageRequest
import com.damaba.damaba.application.port.inbound.user.UpdateUserProfileUseCase
import com.damaba.damaba.mapper.ImageMapper
import io.swagger.v3.oas.annotations.media.Schema

data class UpdateMyProfileRequest(
    @Schema(description = "닉네임", example = "치와와")
    val nickname: String,

    @Schema(description = "인스타 아이디", example = "damaba.unofficial")
    val instagramId: String?,

    @Schema(description = "프로필 이미지")
    val profileImage: ImageRequest,
) {
    fun toCommand(requestUserId: Long): UpdateUserProfileUseCase.Command = UpdateUserProfileUseCase.Command(
        userId = requestUserId,
        nickname = this.nickname,
        instagramId = this.instagramId,
        profileImage = ImageMapper.INSTANCE.toImage(profileImage),
    )
}
