package com.damaba.damaba.controller.common.dto

import io.swagger.v3.oas.annotations.media.Schema

data class ImageResponse(
    @Schema(description = "Image name", example = "apple.jpg")
    val name: String,

    @Schema(description = "Image url", example = "https://damaba-file-server/apple.jpg")
    val url: String,
)
