package com.damaba.user.adapter.inbound.auth.dto

import com.damaba.user.adapter.inbound.user.dto.UserResponse
import io.swagger.v3.oas.annotations.media.Schema

data class OAuthLoginResponse(
    @Schema(description = "회원가입 과정(닉네임, 성별 입력 등)을 완료했는지 여부")
    val isRegistrationCompleted: Boolean,

    @Schema(description = "로그인한 유저 정보")
    val user: UserResponse,

    @Schema(description = "Access token")
    val accessToken: AuthTokenResponse,

    @Schema(description = "Refresh token")
    val refreshToken: AuthTokenResponse,
)
