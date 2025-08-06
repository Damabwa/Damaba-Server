package com.damaba.damaba.controller.file

import com.damaba.damaba.application.file.FileService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "파일 관련 API")
@RestController
class FileController(private val fileService: FileService) {
    @Operation(
        summary = "Upload files",
        description = "<p>파일을 업로드합니다." +
            "<p>요청 시, 업로드하는 파일의 성격에 맞는 <code>fileType</code>을 설정해야 합니다." +
            "<p><strong>만약 <code>fileType</code>에 적절한 항목이 없다면 반드시 서버 관리자에게 문의 부탁드립니다.</strong>",
        security = [SecurityRequirement(name = "access-token")],
    )
    @PostMapping("/api/v1/files", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadFilesV1(@ModelAttribute request: UploadFilesRequest): ResponseEntity<UploadFilesResponse> {
        val uploadedFiles = fileService.uploadFiles(request.toCommand())
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(UploadFilesResponse(uploadedFiles))
    }
}
