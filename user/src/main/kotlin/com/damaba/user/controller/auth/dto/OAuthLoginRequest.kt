package com.damaba.user.controller.auth.dto

import com.damaba.user.domain.user.constant.LoginType
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

data class OAuthLoginRequest(
    @Schema(description = "로그인 종류")
    val loginType: LoginType,

    @Schema(
        description = "<p>OAuth login을 위한 auth key.</p>" +
            "<p>OAuth 로그인 플랫폼별 auth key의 의미는 다음과 같음.</p>" +
            "<ul>" +
            "<li>Kakao: kakao에서 발급받은 access token</li>" +
            "</ul>",
    )
    @field:NotBlank
    val authKey: String,
)
