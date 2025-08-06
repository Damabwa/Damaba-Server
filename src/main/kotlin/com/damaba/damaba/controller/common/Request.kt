package com.damaba.damaba.controller.common

import io.swagger.v3.oas.annotations.media.Schema

data class AddressRequest(
    @Schema(description = "시/도 이름", example = "경기")
    val sido: String,

    @Schema(description = "시/군/구 이름", example = "성남시 분당구")
    val sigungu: String,

    @Schema(description = "도로명 주소", example = "경기 성남시 분당구 판교역로 166")
    val roadAddress: String,

    @Schema(description = "지번 주소", example = "경기 성남시 분당구 백현동 532")
    val jibunAddress: String,
)

data class ImageRequest(
    @Schema(description = "Image name", example = "apple.jpg")
    val name: String,

    @Schema(description = "Image url", example = "https://damaba-file-server/apple.jpg")
    val url: String,
)
