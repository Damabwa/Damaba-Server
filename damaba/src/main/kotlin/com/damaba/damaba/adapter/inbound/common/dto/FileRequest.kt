package com.damaba.damaba.adapter.inbound.common.dto

import com.damaba.common_file.domain.File
import io.swagger.v3.oas.annotations.media.Schema

data class FileRequest(
    @Schema(description = "파일 이름", example = "apple.jpg")
    val name: String,

    @Schema(description = "파일 URL", example = "https://damaba-file-server/apple.jpg")
    val url: String,
) {
    fun toDomain(): File = File(name, url)
}
