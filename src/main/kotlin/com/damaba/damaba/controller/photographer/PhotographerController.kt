package com.damaba.damaba.controller.photographer

import com.damaba.damaba.application.port.inbound.photographer.ExistsPhotographerNicknameUseCase
import com.damaba.damaba.application.port.inbound.photographer.FindPhotographerListUseCase
import com.damaba.damaba.application.port.inbound.photographer.FindSavedPhotographerListUseCase
import com.damaba.damaba.application.port.inbound.photographer.GetPhotographerUseCase
import com.damaba.damaba.application.port.inbound.photographer.RegisterPhotographerUseCase
import com.damaba.damaba.application.port.inbound.photographer.SavePhotographerUseCase
import com.damaba.damaba.application.port.inbound.photographer.UnsavePhotographerUseCase
import com.damaba.damaba.application.port.inbound.photographer.UpdatePhotographerPageUseCase
import com.damaba.damaba.application.port.inbound.photographer.UpdatePhotographerProfileUseCase
import com.damaba.damaba.controller.photographer.request.RegisterPhotographerRequest
import com.damaba.damaba.controller.photographer.request.UpdateMyPhotographerPageRequest
import com.damaba.damaba.controller.photographer.request.UpdateMyPhotographerProfileRequest
import com.damaba.damaba.controller.photographer.response.ExistsPhotographerNicknameResponse
import com.damaba.damaba.controller.photographer.response.PhotographerResponse
import com.damaba.damaba.domain.common.Pagination
import com.damaba.damaba.domain.common.constant.PhotographyType
import com.damaba.damaba.domain.exception.ValidationException
import com.damaba.damaba.domain.photographer.PhotographerListItemResponse
import com.damaba.damaba.domain.photographer.constant.PhotographerSortType
import com.damaba.damaba.domain.region.RegionFilterCondition
import com.damaba.damaba.domain.user.User
import com.damaba.damaba.mapper.PhotographerMapper
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

@Tag(name = "사진작가 관련 API")
@RestController
class PhotographerController(
    private val getPhotographerUseCase: GetPhotographerUseCase,
    private val findPhotographerListUseCase: FindPhotographerListUseCase,
    private val findSavedPhotographerListUseCase: FindSavedPhotographerListUseCase,
    private val existsPhotographerNicknameUseCase: ExistsPhotographerNicknameUseCase,
    private val registerPhotographerUseCase: RegisterPhotographerUseCase,
    private val updatePhotographerProfileUseCase: UpdatePhotographerProfileUseCase,
    private val updatePhotographerPageUseCase: UpdatePhotographerPageUseCase,

    private val savePhotographerUseCase: SavePhotographerUseCase,
    private val unsavePhotographerUseCase: UnsavePhotographerUseCase,
) {
    @Operation(
        summary = "사진작가 조회",
        description = "사진작가를 조회합니다.",
    )
    @ApiResponses(
        ApiResponse(responseCode = "200"),
        ApiResponse(responseCode = "404", description = "`photographerId`에 해당하는 사진작가를 찾을 수 없는 경우", content = []),
    )
    @GetMapping("/api/v1/photographers/{photographerId}")
    fun getPhotographerProfileV1(@PathVariable photographerId: Long): PhotographerResponse {
        val photographer = getPhotographerUseCase.getPhotographer(photographerId)
        return PhotographerMapper.INSTANCE.toPhotographerResponse(photographer)
    }

    @Operation(
        summary = "사진작가 리스트 조회",
        description = "'작가님을 만나봐 페이지' > '작가 리스트'에서 필요한 작가 리스트 정보를 조회합니다.",
    )
    @GetMapping("/api/v1/photographers/list")
    fun findPhotographerListV1(
        @AuthenticationPrincipal
        reqUser: User?,
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
        @RequestParam(defaultValue = "LATEST")
        @Parameter(description = "정렬 기준")
        sort: PhotographerSortType,
        @RequestParam(defaultValue = "0")
        @Parameter(description = "페이지 번호. 0부터 시작합니다.")
        page: Int,
        @RequestParam(defaultValue = "10")
        @Parameter(description = "페이지 크기")
        pageSize: Int,
    ): Pagination<PhotographerListItemResponse> {
        val regionConditions = regions?.map { region ->
            val parts = region.trim().split(" ")
            when (parts.size) {
                1 -> RegionFilterCondition(parts[0], null)
                2 -> RegionFilterCondition(parts[0], parts[1])
                else -> throw ValidationException("지역 정보에 불필요한 공백이 포함되어있습니다.")
            }
        }?.toSet()

        val photographerList = findPhotographerListUseCase.findPhotographerList(
            FindPhotographerListUseCase.Query(
                requestUserId = reqUser?.id,
                regions = regionConditions ?: emptySet(),
                photographyTypes = photographyTypes ?: emptySet(),
                sort = sort,
                page = page,
                pageSize = pageSize,
            ),
        )
        return photographerList.map { PhotographerMapper.INSTANCE.toPhotographerListItemResponse(it) }
    }

    @Operation(
        summary = "저장한 사진작가 리스트 조회",
        description = "저장한 사진작가들을 조회합니다.",
        security = [SecurityRequirement(name = "access-token")],
    )
    @GetMapping("/api/v1/photographers/saved")
    fun findSavedPhotographerListV1(
        @AuthenticationPrincipal requestUser: User,
        @RequestParam(defaultValue = "0") @Parameter(description = "페이지 번호. 0부터 시작합니다.") page: Int,
        @RequestParam(defaultValue = "10") @Parameter(description = "페이지 크기") pageSize: Int,
    ): Pagination<PhotographerListItemResponse> {
        val photographerList = findSavedPhotographerListUseCase.findSavedPhotographerList(
            FindSavedPhotographerListUseCase.Query(
                requestUserId = requestUser.id,
                page = page,
                pageSize = pageSize,
            ),
        )
        return photographerList.map { PhotographerMapper.INSTANCE.toPhotographerListItemResponse(it) }
    }

    @Operation(
        summary = "닉네임(상호명/활동명) 사용 여부 확인",
        description = "사용중인 닉네임(상호명/활동명)인지 확인합니다.",
    )
    @GetMapping("/api/v1/photographers/nicknames/existence")
    fun existsPhotographerNicknameV1(
        @Parameter(description = "사용 여부를 확인할 닉네임", example = "홍길동")
        @RequestParam nickname: String,
    ): ExistsPhotographerNicknameResponse {
        val doesNicknameExists = existsPhotographerNicknameUseCase.existsNickname(
            ExistsPhotographerNicknameUseCase.Query(nickname),
        )
        return ExistsPhotographerNicknameResponse(nickname, doesNicknameExists)
    }

    @Operation(
        summary = "사진작가 등록(회원가입)",
        description = "<p>유저 회원가입 시에만 한 번 사용하며, 사진작가 등록 정보(서비스 이용에 필요한 기본 정보)를 받아 설정합니다." +
            "<p>사진작가의 회원가입에만 사용해야 하며, 일반 유저라면 유저 등록 API(<code>POST /api/v*/users/me/registration</code>)를 사용해야 합니다.",
        security = [SecurityRequirement(name = "access-token")],
    )
    @ApiResponses(
        ApiResponse(responseCode = "200"),
        ApiResponse(responseCode = "404", description = "유저 정보를 찾을 수 없는 경우", content = [Content()]),
        ApiResponse(
            responseCode = "409",
            description = "<p>이미 등록(가입)된 회원인 경우, 즉 유저 등록 API를 이전에 호출한 적이 있었던 경우" +
                "<p>수정하고자 하는 닉네임이 이미 사용중인 경우",
            content = [Content()],
        ),
    )
    @PostMapping("/api/v1/photographers/me/registration")
    fun registerPhotographerV1(
        @AuthenticationPrincipal requester: User,
        @RequestBody request: RegisterPhotographerRequest,
    ): PhotographerResponse {
        val photographer = registerPhotographerUseCase.register(request.toCommand(requester.id))
        return PhotographerMapper.INSTANCE.toPhotographerResponse(photographer)
    }

    @Operation(
        summary = "사진작가 저장",
        description = "사진작가를 저장합니다.",
        security = [SecurityRequirement(name = "access-token")],
    )
    @ApiResponses(
        ApiResponse(responseCode = "204"),
        ApiResponse(responseCode = "409", description = "이미 저장한 사진작가인 경우", content = [Content()]),
    )
    @PostMapping("/api/v1/photographers/{photographerId}/save")
    fun savePhotographerV1(
        @AuthenticationPrincipal reqUser: User,
        @PathVariable photographerId: Long,
    ): ResponseEntity<Unit> {
        savePhotographerUseCase.savePhotographer(
            SavePhotographerUseCase.Command(requestUserId = reqUser.id, photographerId = photographerId),
        )
        return ResponseEntity.noContent().build()
    }

    @Operation(
        summary = "내 작가 프로필 수정",
        description = "<p>내 작가 프로필을 수정합니다. 요청된 정보들로 기존 정보를 변경(overwrite)합니다." +
            "<p>정보가 변경되지 않은 항목이더라도 요청 데이터에 모두 담아야 합니다. 그 때문에 변경되지 않은 항목은 기존 값을 그대로 담아 요청해야 합니다.",
        security = [SecurityRequirement(name = "access-token")],
    )
    @ApiResponses(
        ApiResponse(responseCode = "200"),
        ApiResponse(responseCode = "409", description = "변경하려는 닉네임이 이미 사용중인 경우"),
    )
    @PutMapping("/api/v1/photographers/me/profile")
    fun updateMyPhotographerProfileV1(
        @AuthenticationPrincipal reqUser: User,
        @RequestBody request: UpdateMyPhotographerProfileRequest,
    ): PhotographerResponse {
        val updatedPhotographer =
            updatePhotographerProfileUseCase.updatePhotographerProfile(request.toCommand(reqUser.id))
        return PhotographerMapper.INSTANCE.toPhotographerResponse(updatedPhotographer)
    }

    @Operation(
        summary = "내 작가 페이지 수정",
        description = "<p>내 작가 페이지를 수정합니다. 요청된 정보들로 기존 정보를 변경(overwrite)합니다." +
            "<p>정보가 변경되지 않은 항목이더라도 요청 데이터에 모두 담아야 합니다. 그 때문에 변경되지 않은 항목은 기존 값을 그대로 담아 요청해야 합니다.",
        security = [SecurityRequirement(name = "access-token")],
    )
    @PutMapping("/api/v1/photographers/me/page")
    fun updateMyPhotographerPageV1(
        @AuthenticationPrincipal reqUser: User,
        @RequestBody request: UpdateMyPhotographerPageRequest,
    ): PhotographerResponse {
        val updatedPhotographer = updatePhotographerPageUseCase.updatePhotographerPage(request.toCommand(reqUser.id))
        return PhotographerMapper.INSTANCE.toPhotographerResponse(updatedPhotographer)
    }

    @Operation(
        summary = "사진작가 저장 해제",
        description = "사진작가 저장을 해제합니다.",
        security = [SecurityRequirement(name = "access-token")],
    )
    @ApiResponses(
        ApiResponse(responseCode = "204"),
        ApiResponse(responseCode = "404", description = "사진작가 저장 이력을 찾을 수 없는 경우.", content = [Content()]),
    )
    @DeleteMapping("/api/v1/photographers/{photographerId}/unsave")
    fun unsavePhotographerV1(
        @AuthenticationPrincipal reqUser: User,
        @PathVariable photographerId: Long,
    ): ResponseEntity<Unit> {
        unsavePhotographerUseCase.unsavePhotographer(
            UnsavePhotographerUseCase.Command(requestUserId = reqUser.id, photographerId = photographerId),
        )
        return ResponseEntity.noContent().build()
    }
}
