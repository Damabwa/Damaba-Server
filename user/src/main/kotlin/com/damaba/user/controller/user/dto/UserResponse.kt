package com.damaba.user.controller.user.dto

import com.damaba.user.domain.user.User
import com.damaba.user.domain.user.constant.Gender
import com.damaba.user.domain.user.constant.LoginType
import io.swagger.v3.oas.annotations.media.Schema

data class UserResponse(
    @Schema(description = "Id of user", example = "1")
    val id: Long,

    @Schema(description = "사용하는 로그인 종류")
    val loginType: LoginType,

    @Schema(description = "닉네임", example = "홍길동")
    val nickname: String,

    @Schema(description = "성별")
    val gender: Gender,

    @Schema(description = "나이", example = "20")
    val age: Int,

    @Schema(description = "(Nullable) 인스타 아이디", example = "damaba.official")
    val instagramId: String?,
) {
    companion object {
        fun from(user: User): UserResponse = UserResponse(
            id = user.id,
            loginType = user.loginType,
            nickname = user.nickname,
            gender = user.gender,
            age = user.age,
            instagramId = user.instagramId,
        )
    }
}
