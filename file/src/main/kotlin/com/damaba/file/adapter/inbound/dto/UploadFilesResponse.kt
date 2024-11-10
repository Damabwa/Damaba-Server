package com.damaba.file.adapter.inbound.dto

import com.damaba.common_file.domain.File
import io.swagger.v3.oas.annotations.media.Schema

data class UploadFilesResponse(
    @Schema(description = "업로드된 파일 리스트")
    val files: List<File>,
)
