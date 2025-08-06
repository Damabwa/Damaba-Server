package com.damaba.damaba.controller.term

import com.damaba.damaba.application.term.TermService
import com.damaba.damaba.domain.term.TermType
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "서비스 약관 동의 API")
@RestController
class TermController(
    private val termService: TermService,
) {
    @Operation(
        summary = "약관 메타데이터 조회",
        description = "모든 약관 종류(type), 필수 여부(required)를 반환한다.",
    )
    @ApiResponses(
        ApiResponse(responseCode = "200"),
    )
    @GetMapping("/api/v1/terms/metadata")
    fun getTermMetadata(): ResponseEntity<List<TermMetadataResponse>> {
        val metadata = TermType.values().map {
            TermMetadataResponse(
                type = it,
                required = it.required,
            )
        }
        return ResponseEntity.ok(metadata)
    }
}
