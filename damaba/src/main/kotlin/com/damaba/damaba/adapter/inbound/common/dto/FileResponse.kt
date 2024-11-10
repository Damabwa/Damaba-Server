package com.damaba.damaba.adapter.inbound.common.dto

import io.swagger.v3.oas.annotations.media.Schema

data class FileResponse(
    @Schema(description = "파일 이름", example = "apple.jpg")
    val name: String,

    @Schema(description = "파일 URL", example = "https://damaba-file-server/apple.jpg")
    val url: String,
)
