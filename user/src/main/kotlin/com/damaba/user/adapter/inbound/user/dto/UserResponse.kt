package com.damaba.user.adapter.inbound.user.dto

import com.damaba.user.domain.user.User
import com.damaba.user.domain.user.constant.Gender
import com.damaba.user.domain.user.constant.LoginType
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

data class UserResponse(
    @Schema(description = "Id of user", example = "1")
    val id: Long,

    @Schema(description = "사용하는 로그인 종류")
    val loginType: LoginType,

    @Schema(description = "닉네임", example = "홍길동")
    val nickname: String,

    @Schema(description = "프로필 이미지 url", example = "https://user-profile-image-url")
    val profileImageUrl: String,

    @Schema(description = "성별")
    val gender: Gender,

    @Schema(description = "생년월일")
    val birthDate: LocalDate,

    @Schema(description = "(Nullable) 인스타 아이디", example = "damaba.official")
    val instagramId: String?,
) {
    companion object {
        fun from(user: User): UserResponse = UserResponse(
            id = user.id,
            loginType = user.loginType,
            nickname = user.nickname,
            profileImageUrl = user.profileImageUrl,
            gender = user.gender,
            birthDate = user.birthDate,
            instagramId = user.instagramId,
        )
    }
}
