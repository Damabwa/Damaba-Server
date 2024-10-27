package com.damaba.user.adapter.inbound.auth.dto

import com.damaba.user.adapter.inbound.user.dto.UserResponse
import com.damaba.user.domain.auth.AuthToken
import com.damaba.user.domain.user.User
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
) {
    companion object {
        fun from(user: User, accessToken: AuthToken, refreshToken: AuthToken): OAuthLoginResponse =
            OAuthLoginResponse(
                isRegistrationCompleted = user.isRegistrationCompleted,
                user = UserResponse.from(user),
                accessToken = AuthTokenResponse.from(accessToken),
                refreshToken = AuthTokenResponse.from(refreshToken),
            )
    }
}
