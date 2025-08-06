package com.damaba.damaba.controller.user

import com.damaba.damaba.application.term.TermItem
import com.damaba.damaba.application.user.RegisterUserCommand
import com.damaba.damaba.application.user.UpdateUserProfileCommand
import com.damaba.damaba.controller.common.ImageRequest
import com.damaba.damaba.controller.term.AgreementRequestItem
import com.damaba.damaba.domain.user.constant.Gender
import com.damaba.damaba.mapper.ImageMapper
import io.swagger.v3.oas.annotations.media.Schema

data class RegisterUserRequest(
    @Schema(description = "닉네임", example = "치와와")
    val nickname: String,

    @Schema(description = "성별")
    val gender: Gender,

    @Schema(description = "인스타 아이디", example = "damaba.unofficial")
    val instagramId: String?,

    @Schema(description = "동의한 약관 목록")
    val agreements: List<AgreementRequestItem>,
) {
    fun toCommand(requestUserId: Long) = RegisterUserCommand(
        userId = requestUserId,
        nickname = nickname,
        gender = gender,
        instagramId = instagramId,
        terms = agreements.map { TermItem(it.type, it.agreed) },
    )
}

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

data class AgreementRequestItem(
    @Schema(description = "약관 종류", example = "SERVICE_TERMS")
    val type: String,

    @Schema(description = "사용자 동의 여부", example = "true")
    val agreed: Boolean,
)
