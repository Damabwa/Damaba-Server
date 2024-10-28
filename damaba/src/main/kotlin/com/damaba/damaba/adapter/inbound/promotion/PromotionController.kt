package com.damaba.damaba.adapter.inbound.promotion

import com.damaba.damaba.adapter.inbound.promotion.dto.PostPromotionRequest
import com.damaba.damaba.adapter.inbound.promotion.dto.PromotionResponse
import com.damaba.damaba.application.port.inbound.promotion.PostPromotionUseCase
import com.damaba.user.domain.user.User
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@Tag(name = "프로모션 관련 API")
@RestController
class PromotionController(private val postPromotionUseCase: PostPromotionUseCase) {
    @Operation(
        summary = "프로모션 등록",
        description = "신규 프로모션을 등록합니다.",
        security = [SecurityRequirement(name = "access-token")],
    )
    @PostMapping("/api/v1/promotions", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun postPromotionV1(
        @AuthenticationPrincipal requestUser: User,
        @ModelAttribute request: PostPromotionRequest,
    ): ResponseEntity<PromotionResponse> {
        val promotion = postPromotionUseCase.postPromotion(request.toCommand(requestUser.id))
        return ResponseEntity
            .created(URI.create("/api/v*/promotions/${promotion.id}"))
            .body(PromotionResponse.from(promotion))
    }
}
