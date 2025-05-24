package com.damaba.damaba.controller.user.response

import com.damaba.damaba.controller.common.response.ImageResponse
import com.damaba.damaba.domain.user.constant.Gender
import com.damaba.damaba.domain.user.constant.LoginType
import com.damaba.damaba.domain.user.constant.UserRoleType
import com.damaba.damaba.domain.user.constant.UserType
import io.swagger.v3.oas.annotations.media.Schema

data class UserResponse(
    @Schema(description = "Id of user", example = "1")
    val id: Long,

    @Schema(description = "User type")
    val type: UserType,

    @Schema(description = "User roles")
    val roles: Set<UserRoleType>,

    @Schema(description = "사용하는 로그인 종류")
    val loginType: LoginType,

    @Schema(description = "닉네임", example = "홍길동")
    val nickname: String,

    @Schema(description = "프로필 이미지")
    val profileImage: ImageResponse?,

    @Schema(description = "성별")
    val gender: Gender,

    @Schema(description = "(Nullable) 인스타 아이디", example = "damaba.official")
    val instagramId: String?,
)
