package com.damaba.damaba.controller.file.response

import com.damaba.damaba.domain.file.File
import io.swagger.v3.oas.annotations.media.Schema

data class UploadFilesResponse(
    @Schema(description = "업로드된 파일 리스트")
    val files: List<File>,
)
