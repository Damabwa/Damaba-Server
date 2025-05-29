package com.damaba.damaba.controller.promotion

import com.damaba.damaba.application.promotion.PromotionService
import com.damaba.damaba.application.promotion.dto.DeletePromotionCommand
import com.damaba.damaba.application.promotion.dto.FindPromotionListQuery
import com.damaba.damaba.application.promotion.dto.FindSavedPromotionListQuery
import com.damaba.damaba.application.promotion.dto.GetPromotionDetailQuery
import com.damaba.damaba.application.promotion.dto.SavePromotionCommand
import com.damaba.damaba.application.promotion.dto.UnsavePromotionCommand
import com.damaba.damaba.controller.promotion.dto.PostPromotionRequest
import com.damaba.damaba.controller.promotion.dto.PromotionDetailResponse
import com.damaba.damaba.controller.promotion.dto.PromotionListItemResponse
import com.damaba.damaba.controller.promotion.dto.PromotionResponse
import com.damaba.damaba.controller.promotion.dto.UpdatePromotionRequest
import com.damaba.damaba.domain.common.Pagination
import com.damaba.damaba.domain.common.constant.PhotographyType
import com.damaba.damaba.domain.exception.ValidationException
import com.damaba.damaba.domain.promotion.constant.PromotionProgressStatus
import com.damaba.damaba.domain.promotion.constant.PromotionSortType
import com.damaba.damaba.domain.promotion.constant.PromotionType
import com.damaba.damaba.domain.region.RegionFilterCondition
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
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@Tag(name = "프로모션 관련 API")
@RestController
class PromotionController(private val promotionService: PromotionService) {
    @Operation(
        summary = "프로모션 단건 조회",
        description = "<p><code>promotionId</code>에 해당하는 프로모션을 단건 조회합니다." +
            "<p>프로모션 상세 정보가 필요한 경우 '프로모션 상세 조회 API'를 사용해주세요.",
    )
    @ApiResponses(
        ApiResponse(responseCode = "200"),
        ApiResponse(responseCode = "404", description = "`promotionId`에 일치하는 프로모션이 없는 경우", content = [Content()]),
    )
    @GetMapping("/api/v1/promotions/{promotionId}")
    fun getPromotionV1(@PathVariable promotionId: Long): PromotionResponse {
        val promotion = promotionService.getPromotion(promotionId)
        return PromotionMapper.INSTANCE.toPromotionResponse(promotion)
    }

    @Operation(
        summary = "프로모션 상세 조회",
        description = "<p><code>promotionId</code>에 해당하는 프로모션의 상세 정보를 조회합니다." +
            "<p>프로모션 상세 조회 시 조회수가 1 증가합니다.",
    )
    @ApiResponses(
        ApiResponse(responseCode = "200"),
        ApiResponse(responseCode = "404", description = "`promotionId`에 일치하는 프로모션이 없는 경우", content = [Content()]),
    )
    @GetMapping("/api/v1/promotions/{promotionId}/details")
    fun getPromotionDetailV1(
        @AuthenticationPrincipal requestUser: User?,
        @PathVariable promotionId: Long,
    ): PromotionDetailResponse {
        val promotionDetail = promotionService.getPromotionDetail(
            GetPromotionDetailQuery(
                requestUserId = requestUser?.id,
                promotionId = promotionId,
            ),
        )
        return PromotionMapper.INSTANCE.toPromotionDetailResponse(promotionDetail)
    }

    @Operation(
        summary = "프로모션 리스트 조회",
        description = "'Event로 담아봐 페이지' > '이벤트 리스트'에서 필요한 프로모션 리스트 정보를 조회합니다.",
    )
    @GetMapping("/api/v1/promotions/list")
    fun findPromotionListV1(
        @AuthenticationPrincipal
        reqUser: User?,
        @RequestParam(required = false)
        @Parameter(description = "프로모션 유형")
        type: PromotionType?,
        @RequestParam(required = false)
        @Parameter(description = "진행 상태")
        progressStatus: PromotionProgressStatus?,
        @RequestParam(required = false)
        @Parameter(
            description = "<p>지역 리스트." +
                "<p>카테고리(시/도)와 지역 이름(시/군/구)는 공백(<code> </code>)으로 구분하여 전달해야 합니다." +
                "<p>지역 이름이 없는 경우(ex. 서울 전체에 대한 필터링)에는 <code>\"서울\"</code>과 같이 카테고리만 전달합니다.",
            example = """["서울 강남구", "서울 은평구", "대전"]""",
        ) regions: List<String>?,
        @RequestParam(required = false)
        @Parameter(description = "촬영 종류")
        photographyTypes: Set<PhotographyType>?,
        @RequestParam(required = false)
        @Parameter(description = "검색 키워드")
        searchKeyword: String?,
        @RequestParam(defaultValue = "LATEST")
        @Parameter(description = "정렬 기준")
        sortType: PromotionSortType,
        @RequestParam(defaultValue = "0")
        @Parameter(description = "페이지 번호. 0부터 시작합니다.")
        page: Int,
        @RequestParam(defaultValue = "10")
        @Parameter(description = "페이지 크기")
        pageSize: Int,
    ): Pagination<PromotionListItemResponse> {
        val regionConditions = regions?.map { region ->
            val parts = region.trim().split(" ")
            when (parts.size) {
                1 -> RegionFilterCondition(parts[0], null)
                2 -> RegionFilterCondition(parts[0], parts[1])
                else -> throw ValidationException("지역 정보에 불필요한 공백이 포함되어있습니다.")
            }
        }?.toSet()

        val promotionList = promotionService.findPromotionList(
            FindPromotionListQuery(
                requestUserId = reqUser?.id,
                type = type,
                progressStatus = progressStatus,
                regions = regionConditions ?: emptySet(),
                photographyTypes = photographyTypes ?: emptySet(),
                searchKeyword = searchKeyword,
                sortType = sortType,
                page = page,
                pageSize = pageSize,
            ),
        )
        return promotionList.map { PromotionMapper.INSTANCE.toPromotionListItemResponse(it) }
    }

    @Operation(
        summary = "저장한 프로모션 목록 조회",
        description = "저장한 프로모션들을 조회합니다.",
        security = [SecurityRequirement(name = "access-token")],
    )
    @GetMapping("/api/v1/promotions/saved")
    fun findSavedPromotionListV1(
        @AuthenticationPrincipal requestUser: User,
        @RequestParam(defaultValue = "0") @Parameter(description = "페이지 번호. 0부터 시작합니다.") page: Int,
        @RequestParam(defaultValue = "10") @Parameter(description = "페이지 크기") pageSize: Int,
    ): Pagination<PromotionListItemResponse> {
        val promotionList = promotionService.findSavedPromotionList(
            FindSavedPromotionListQuery(
                requestUserId = requestUser.id,
                page = page,
                pageSize = pageSize,
            ),
        )
        return promotionList.map { PromotionMapper.INSTANCE.toPromotionListItemResponse(it) }
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
        val promotion = promotionService.postPromotion(request.toCommand(requestUser.id))
        return ResponseEntity
            .created(URI.create("/api/v*/promotions/${promotion.id}"))
            .body(PromotionMapper.INSTANCE.toPromotionResponse(promotion))
    }

    @Operation(
        summary = "프로모션 저장",
        description = "프로모션을 저장합니다.",
        security = [SecurityRequirement(name = "access-token")],
    )
    @ApiResponses(
        ApiResponse(responseCode = "204"),
        ApiResponse(responseCode = "409", description = "이미 저장된 프로모션인 경우", content = [Content()]),
    )
    @PostMapping("/api/v1/promotions/{promotionId}/save")
    fun savePromotionV1(
        @AuthenticationPrincipal requestUser: User,
        @PathVariable promotionId: Long,
    ): ResponseEntity<Unit> {
        promotionService.savePromotion(SavePromotionCommand(requestUser.id, promotionId))
        return ResponseEntity.noContent().build()
    }

    @Operation(
        summary = "프로모션 수정",
        description = "<p>프로모션을 수정합니다. 요청된 정보들로 기존 정보를 변경(overwrite)합니다." +
            "<p>정보가 변경되지 않은 항목이더라도 요청 데이터에 모두 담아야 합니다. 그 때문에 변경되지 않은 항목은 기존 값을 그대로 담아 요청해야 합니다.",
        security = [SecurityRequirement(name = "access-token")],
    )
    @ApiResponses(
        ApiResponse(responseCode = "200"),
        ApiResponse(
            responseCode = "403",
            description = "프로모션 수정 권한이 없는 경우. 프로모션은 작성자만 수정할 수 있음.",
            content = [Content()],
        ),
        ApiResponse(
            responseCode = "404",
            description = "<code>promotionId</code>에 해당하는 프로모션을 찾을 수 없는 경우",
            content = [Content()],
        ),
    )
    @PutMapping("/api/v1/promotions/{promotionId}")
    fun updatePromotionV1(
        @AuthenticationPrincipal requestUser: User,
        @PathVariable promotionId: Long,
        @RequestBody request: UpdatePromotionRequest,
    ): PromotionResponse {
        val promotion = promotionService.updatePromotion(
            request.toCommand(
                requestUserId = requestUser.id,
                promotionId = promotionId,
            ),
        )
        return PromotionMapper.INSTANCE.toPromotionResponse(promotion = promotion)
    }

    @Operation(
        summary = "프로모션 삭제",
        description = "프로모션을 삭제합니다.",
        security = [SecurityRequirement(name = "access-token")],
    )
    @ApiResponses(
        ApiResponse(responseCode = "204"),
        ApiResponse(
            responseCode = "403",
            description = "프로모션 삭제 권한이 없는 경우. 프로모션은 작성자 또는 관리자만 삭제할 수 있음.",
            content = [Content()],
        ),
        ApiResponse(
            responseCode = "404",
            description = "<code>promotionId</code>에 해당하는 프로모션을 찾을 수 없는 경우",
            content = [Content()],
        ),
    )
    @DeleteMapping("/api/v1/promotions/{promotionId}")
    fun deletePromotionV1(
        @AuthenticationPrincipal requestUser: User,
        @PathVariable promotionId: Long,
    ): ResponseEntity<Unit> {
        promotionService.deletePromotion(
            DeletePromotionCommand(
                requestUser = requestUser,
                promotionId = promotionId,
            ),
        )
        return ResponseEntity.noContent().build()
    }

    @Operation(
        summary = "프로모션 저장 해제",
        description = "저장된 프로모션을 저장 해제합니다..",
        security = [SecurityRequirement(name = "access-token")],
    )
    @ApiResponses(
        ApiResponse(responseCode = "204"),
        ApiResponse(responseCode = "404", description = "프로모션 저장 이력이 존재하지 않는 경우", content = [Content()]),
    )
    @DeleteMapping("/api/v1/promotions/{promotionId}/unsave")
    fun unsavePromotionV1(
        @AuthenticationPrincipal requestUser: User,
        @PathVariable promotionId: Long,
    ): ResponseEntity<Unit> {
        promotionService.unsavePromotion(UnsavePromotionCommand(requestUser.id, promotionId))
        return ResponseEntity.noContent().build()
    }
}
