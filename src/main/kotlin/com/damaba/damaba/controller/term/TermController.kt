package com.damaba.damaba.controller.term

import com.damaba.damaba.application.term.TermService
import com.damaba.damaba.domain.term.TermType
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "서비스 약관 동의 관련 API")
@RestController
@RequestMapping("/api/v1/terms")
class TermController(
    private val termService: TermService,
) {
    /**
     * 일반 유저의 서비스 약관 동의 목록 조회 (type, required)
     */
    @Operation(
        summary = "회원용 약관 목록 조회",
        description = "가입 시 일반 유저가 동의해야 하는 약관 동의 목록 조회",
    )
    @ApiResponses(
        ApiResponse(responseCode = "200"),
    )
    @GetMapping("/user")
    fun getUserTerms(): ResponseEntity<List<TermMetadataResponse>> {
        val userTerms = TermType.values()
            .filter { it != TermType.PHOTOGRAPHER_TERMS }
            .map { TermMetadataResponse(type = it, required = it.required) }
        return ResponseEntity.ok(userTerms)
    }

    /**
     * 사진 작가의 서비스 약관 동의 목록 조회 (type, required)
     */
    @Operation(
        summary = "작가용 약관 목록 조회",
        description = "가입 시 작가가 동의해야 하는 약관 동의 목록 조회",
    )
    @ApiResponses(
        ApiResponse(responseCode = "200"),
    )
    @GetMapping("/photographer")
    fun getPhotographerTerms(): ResponseEntity<List<TermMetadataResponse>> {
        val photographerTerms = TermType.values()
            .map { TermMetadataResponse(type = it, required = it.required) }
        return ResponseEntity.ok(photographerTerms)
    }
}
