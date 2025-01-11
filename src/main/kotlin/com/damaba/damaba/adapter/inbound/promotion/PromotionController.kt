package com.damaba.damaba.adapter.inbound.promotion

import com.damaba.damaba.adapter.inbound.promotion.dto.PostPromotionRequest
import com.damaba.damaba.adapter.inbound.promotion.dto.PromotionResponse
import com.damaba.damaba.application.port.inbound.promotion.FindPromotionsUseCase
import com.damaba.damaba.application.port.inbound.promotion.GetPromotionUseCase
import com.damaba.damaba.application.port.inbound.promotion.PostPromotionUseCase
import com.damaba.damaba.domain.common.Pagination
import com.damaba.damaba.domain.user.User
import com.damaba.damaba.mapper.PromotionMapper
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@Tag(name = "프로모션 관련 API")
@RestController
class PromotionController(
    private val getPromotionUseCase: GetPromotionUseCase,
    private val findPromotionsUseCase: FindPromotionsUseCase,
    private val postPromotionUseCase: PostPromotionUseCase,
) {
    @Operation(summary = "프로모션 상세 조회")
    @ApiResponses(
        ApiResponse(responseCode = "200"),
        ApiResponse(responseCode = "404", description = "`promotionId`에 일치하는 프로모션이 없는 경우", content = [Content()]),
    )
    @GetMapping("/api/v1/promotions/{promotionId}")
    fun getPromotionV1(@PathVariable promotionId: Long): PromotionResponse {
        val promotion = getPromotionUseCase.getPromotion(promotionId)
        return PromotionMapper.INSTANCE.toPromotionResponse(promotion)
    }

    @Operation(summary = "프로모션 리스트 조회")
    @GetMapping("/api/v1/promotions")
    fun findPromotionsV1(
        @RequestParam @Parameter(description = "페이지 번호. 0부터 시작합니다.") page: Int,
        @RequestParam @Parameter(description = "페이지 크기") pageSize: Int,
    ): Pagination<PromotionResponse> {
        val promotions = findPromotionsUseCase.findPromotions(FindPromotionsUseCase.Query(page, pageSize))
        return promotions.map { PromotionMapper.INSTANCE.toPromotionResponse(it) }
    }

    @Operation(
        summary = "프로모션 등록",
        description = "신규 프로모션을 등록합니다.",
        security = [SecurityRequirement(name = "access-token")],
    )
    @PostMapping("/api/v1/promotions")
    fun postPromotionV1(
        @AuthenticationPrincipal requestUser: User,
        @RequestBody request: PostPromotionRequest,
    ): ResponseEntity<PromotionResponse> {
        val promotion = postPromotionUseCase.postPromotion(request.toCommand(requestUser.id))
        return ResponseEntity
            .created(URI.create("/api/v*/promotions/${promotion.id}"))
            .body(PromotionMapper.INSTANCE.toPromotionResponse(promotion))
    }
}
