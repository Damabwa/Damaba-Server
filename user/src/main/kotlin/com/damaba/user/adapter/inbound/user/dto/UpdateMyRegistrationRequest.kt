package com.damaba.user.adapter.inbound.user.dto

import com.damaba.user.application.port.inbound.user.RegisterUserUseCase
import com.damaba.user.domain.user.constant.Gender
import io.swagger.v3.oas.annotations.media.Schema

data class UpdateMyRegistrationRequest(
    @Schema(description = "닉네임", example = "치와와")
    val nickname: String,

    @Schema(description = "성별")
    val gender: Gender,

    @Schema(description = "인스타 아이디", example = "damaba.unofficial")
    val instagramId: String?,
) {
    fun toCommand(requestUserId: Long) = RegisterUserUseCase.Command(
        userId = requestUserId,
        nickname = nickname,
        gender = gender,
        instagramId = instagramId,
    )
}
