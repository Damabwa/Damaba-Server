package com.damaba.user.adapter.inbound.user.dto

import com.damaba.user.application.port.inbound.user.UpdateMyInfoUseCase
import com.damaba.user.domain.user.UserProfileImage
import io.swagger.v3.oas.annotations.media.Schema

data class UpdateMyInfoRequest(
    @Schema(description = "닉네임", example = "치와와")
    val nickname: String,

    @Schema(description = "인스타 아이디", example = "damaba.unofficial")
    val instagramId: String?,

    @Schema(description = "프로필 이미지")
    val profileImage: UserProfileImage,
) {
    fun toCommand(requestUserId: Long): UpdateMyInfoUseCase.Command = UpdateMyInfoUseCase.Command(
        userId = requestUserId,
        nickname = this.nickname,
        instagramId = this.instagramId,
        profileImage = profileImage,
    )
}
