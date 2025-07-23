package com.damaba.damaba.controller.term

import com.damaba.damaba.application.term.TermService
import com.damaba.damaba.domain.term.TermType
import com.damaba.damaba.domain.user.User
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@Tag(name = "서비스 약관 동의 API")
@RestController
class TermController(
    private val termService: TermService,
) {

    @Operation(
        summary = "유저 서비스 약관 동의 제출",
        description = "<p> 일반 유저용 약관 3개 제출",
        security = [SecurityRequirement(name = "access-token")],
    )
    @ApiResponses(
        ApiResponse(responseCode = "200"),
        ApiResponse(responseCode = "404", description = "유저 정보를 찾을 수 없는 경우", content = [Content()]),
    )
    @PostMapping("/api/v1/users/me/terms/agree")
    fun acceptUserTerms(
        @AuthenticationPrincipal requestUser: User,
        @RequestBody request: UserTermRequest,
    ): ResponseEntity<Unit> {
        termService.acceptUserTerms(request.toUserCommand(requestUser.id))
        return ResponseEntity.noContent().build()
    }

    @Operation(
        summary = "작가 서비스 약관 동의 제출",
        description = "<p> 사진 작가용 약관 4개 제출",
        security = [SecurityRequirement(name = "access-token")],
    )
    @ApiResponses(
        ApiResponse(responseCode = "200"),
        ApiResponse(responseCode = "404", description = "유저 정보를 찾을 수 없는 경우", content = [Content()]),
    )
    @PostMapping("/api/v1/photographers/me/terms/agree")
    fun acceptPhotographerTerms(
        @AuthenticationPrincipal requestUser: User,
        @RequestBody request: UserTermRequest,
    ): ResponseEntity<Unit> {
        termService.acceptPhotographerTerms(request.toPhotographerCommand(requestUser.id))
        return ResponseEntity.noContent().build()
    }

    @Operation(
        summary = "약관 메타데이터 조회",
        description = "모든 약관 종류(type), 필수 여부(required), 상세 URL(detailUrl)을 반환한다.",
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
                detailUrl = it.detailUrl,
            )
        }
        return ResponseEntity.ok(metadata)
    }
}
