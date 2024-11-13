package com.damaba.user.adapter.inbound.common.dto

import io.swagger.v3.oas.annotations.media.Schema

data class ImageRequest(
    @Schema(description = "Image name", example = "apple.jpg")
    val name: String,

    @Schema(description = "Image url", example = "https://damaba-file-server/apple.jpg")
    val url: String,
)
