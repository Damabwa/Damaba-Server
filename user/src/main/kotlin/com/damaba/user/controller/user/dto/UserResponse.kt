package com.damaba.user.controller.user.dto

import com.damaba.user.domain.user.User
import com.damaba.user.domain.user.constant.LoginType
import io.swagger.v3.oas.annotations.media.Schema

data class UserResponse(
    @Schema(description = "Id of user", example = "1")
    val id: Long,

    @Schema(description = "사용하는 로그인 종류")
    val loginType: LoginType,
) {
    companion object {
        fun from(user: User): UserResponse = UserResponse(
            id = user.id,
            loginType = user.loginType,
        )
    }
}
