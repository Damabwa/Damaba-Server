package com.damaba.damaba.controller.user.response

import io.swagger.v3.oas.annotations.media.Schema

data class ExistsUserNicknameResponse(
    @Schema(description = "닉네임", example = "말티즈")
    val nickname: String,

    @Schema(description = "닉네임의 사용 여부. 사용중인 닉네임이라면 <code>true</code>, 사용중이지 않다면 <code>false</code>")
    val exists: Boolean,
)
