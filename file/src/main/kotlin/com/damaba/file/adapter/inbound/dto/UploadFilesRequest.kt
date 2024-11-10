package com.damaba.file.adapter.inbound.dto

import com.damaba.file.application.port.inbound.UploadFilesUseCase
import com.damaba.file.domain.FileType
import com.damaba.file.domain.UploadFile
import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.web.multipart.MultipartFile

data class UploadFilesRequest(
    @Schema(description = "업로드할 파일의 유형")
    val fileType: FileType,

    @Schema(description = "업로드할 이미지 리스트")
    val files: List<MultipartFile>,
) {
    fun toCommand() = UploadFilesUseCase.Command(
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
