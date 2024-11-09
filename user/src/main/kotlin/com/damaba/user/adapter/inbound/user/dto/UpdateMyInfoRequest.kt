package com.damaba.user.adapter.inbound.user.dto

import com.damaba.common_file.domain.UploadFile
import com.damaba.user.application.port.inbound.user.UpdateMyInfoUseCase
import com.damaba.user.domain.user.constant.Gender
import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.web.multipart.MultipartFile

data class UpdateMyInfoRequest(
    @Schema(description = "닉네임", example = "치와와")
    val nickname: String?,

    @Schema(description = "성별")
    val gender: Gender?,

    @Schema(description = "인스타 아이디", example = "damaba.unofficial")
    val instagramId: String?,

    @Schema(description = "프로필 이미지")
    val profileImage: MultipartFile?,
) {
    fun toCommand(requestUserId: Long): UpdateMyInfoUseCase.Command {
        var profileImage: UploadFile? = null
        if (this.profileImage != null) {
            profileImage = UploadFile(
                name = this.profileImage.originalFilename,
                size = this.profileImage.size,
                contentType = this.profileImage.contentType,
                inputStream = this.profileImage.inputStream,
            )
        }
        return UpdateMyInfoUseCase.Command(
            userId = requestUserId,
            nickname = this.nickname,
            gender = this.gender,
            instagramId = this.instagramId,
            profileImage = profileImage,
        )
    }
}
