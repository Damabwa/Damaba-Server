package com.damaba.damaba.controller.user.dto

import com.damaba.damaba.application.user.UpdateUserProfileCommand
import com.damaba.damaba.controller.common.dto.ImageRequest
import com.damaba.damaba.mapper.ImageMapper
import io.swagger.v3.oas.annotations.media.Schema

data class UpdateMyProfileRequest(
    @Schema(description = "닉네임", example = "치와와")
    val nickname: String,

    @Schema(description = "인스타 아이디", example = "damaba.unofficial")
    val instagramId: String?,

    @Schema(description = "프로필 이미지")
    val profileImage: ImageRequest?,
) {
    fun toCommand(requestUserId: Long) = UpdateUserProfileCommand(
        userId = requestUserId,
        nickname = this.nickname,
        instagramId = this.instagramId,
        profileImage = profileImage?.let { ImageMapper.INSTANCE.toImage(it) },
    )
}
