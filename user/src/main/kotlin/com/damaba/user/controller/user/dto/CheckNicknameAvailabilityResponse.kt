package com.damaba.user.controller.user.dto

import io.swagger.v3.oas.annotations.media.Schema

data class CheckNicknameAvailabilityResponse(
    @Schema(description = "닉네임", example = "말티즈")
    val nickname: String,

    @Schema(description = "닉네임의 이용가능성. 이용 가능한 닉네임이라면 <code>true</code>, 그렇지 않다면 <code>false</code>")
    val availability: Boolean,
)
