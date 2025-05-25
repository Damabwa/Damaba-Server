package com.damaba.damaba.controller.user.dto

import com.damaba.damaba.application.user.dto.RegisterUserCommand
import com.damaba.damaba.domain.user.constant.Gender
import io.swagger.v3.oas.annotations.media.Schema

data class RegisterUserRequest(
    @Schema(description = "닉네임", example = "치와와")
    val nickname: String,

    @Schema(description = "성별")
    val gender: Gender,

    @Schema(description = "인스타 아이디", example = "damaba.unofficial")
    val instagramId: String?,
) {
    fun toCommand(requestUserId: Long) = RegisterUserCommand(
        userId = requestUserId,
        nickname = nickname,
        gender = gender,
        instagramId = instagramId,
    )
}
