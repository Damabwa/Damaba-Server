package com.damaba.user.controller.user.dto

import com.damaba.user.application.user.UpdateMyInfoUseCase
import com.damaba.user.domain.user.constant.Gender
import io.swagger.v3.oas.annotations.media.Schema

// TODO: 유저 정보 관련 정책 기획이 완료된 후, 적절한 validation 추가
data class UpdateMyInfoRequest(
    @Schema(description = "닉네임", example = "치와와")
    val nickname: String?,

    @Schema(description = "성별")
    val gender: Gender?,

    @Schema(description = "나이", example = "25")
    val age: Int?,

    @Schema(description = "인스타 아이디", example = "damaba.unofficial")
    val instagramId: String?,
) {
    fun toCommand(requestUserId: Long) = UpdateMyInfoUseCase.Command(
        userId = requestUserId,
        nickname = this.nickname,
        gender = this.gender,
        age = this.age,
        instagramId = this.instagramId,
    )
}
