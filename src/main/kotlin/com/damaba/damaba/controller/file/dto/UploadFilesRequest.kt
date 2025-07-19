package com.damaba.damaba.controller.file.dto

import com.damaba.damaba.application.file.UploadFilesCommand
import com.damaba.damaba.domain.file.FileType
import com.damaba.damaba.domain.file.UploadFile
import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.web.multipart.MultipartFile

data class UploadFilesRequest(
    @Schema(description = "업로드할 파일의 유형")
    val fileType: FileType,

    @Schema(description = "업로드할 이미지 리스트")
    val files: List<MultipartFile>,
) {
    fun toCommand() = UploadFilesCommand(
        fileType = fileType,
        files = this.files.map { multipartFile ->
            UploadFile(
                name = multipartFile.originalFilename,
                size = multipartFile.size,
                contentType = multipartFile.contentType,
                inputStream = multipartFile.inputStream,
            )
        },
    )
}
