package com.damaba.damaba.controller.auth.response

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

data class AuthTokenResponse(
    @Schema(description = "token value", example = "eyJ0eXAiOiJKV1QiLC.eyJzdWIiOiIxIiwicm9sZ.Fp7RNDCv9QQghS2cTC")
    val value: String,

    @Schema(description = "토큰 만료 시각")
    val expiresAt: LocalDateTime,
)
