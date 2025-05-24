package com.damaba.damaba.domain.photographer

import com.damaba.damaba.domain.common.constant.PhotographyType
import com.damaba.damaba.domain.file.Image
import io.swagger.v3.oas.annotations.media.Schema

data class PhotographerListItemResponse(
    @Schema(description = "Id of user", example = "1")
    val id: Long,

    @Schema(description = "닉네임", example = "홍길동")
    val nickname: String,

    @Schema(description = "프로필 이미지")
    val profileImage: Image?,

    @Schema(description = "사진작가 저장 여부. 저장된 사진작가라면 <code>true</code>")
    val isSaved: Boolean,

    @Schema(description = "주력 촬영 종류")
    val mainPhotographyTypes: Set<PhotographyType>,
)
