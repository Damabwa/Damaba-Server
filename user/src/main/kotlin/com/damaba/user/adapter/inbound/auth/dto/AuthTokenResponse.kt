package com.damaba.user.adapter.inbound.auth.dto

import com.damaba.user.domain.auth.AuthToken
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

data class AuthTokenResponse(
    @Schema(description = "token value", example = "eyJ0eXAiOiJKV1QiLC.eyJzdWIiOiIxIiwicm9sZ.Fp7RNDCv9QQghS2cTC")
    val value: String,

    @Schema(description = "토큰 만료 시각")
    val expiresAt: LocalDateTime,
) {
    companion object {
        fun from(authToken: AuthToken): AuthTokenResponse =
            AuthTokenResponse(authToken.value, authToken.expiresAt)
    }
}
