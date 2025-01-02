package com.damaba.damaba.adapter.inbound.file

import com.damaba.damaba.adapter.inbound.file.dto.UploadFilesRequest
import com.damaba.damaba.adapter.inbound.file.dto.UploadFilesResponse
import com.damaba.damaba.application.port.inbound.file.UploadFilesUseCase
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
class FileController(private val uploadFilesUseCase: UploadFilesUseCase) {
    @Operation(
        summary = "Upload files",
        security = [SecurityRequirement(name = "access-token")],
    )
    @PostMapping("/api/v1/files", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadFilesV1(@ModelAttribute request: UploadFilesRequest): ResponseEntity<UploadFilesResponse> {
        val uploadedFiles = uploadFilesUseCase.uploadFiles(request.toCommand())
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(UploadFilesResponse(uploadedFiles))
    }
}
