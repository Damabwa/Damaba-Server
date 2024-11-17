package com.damaba.damaba.adapter.inbound.photographer

import com.damaba.damaba.adapter.inbound.photographer.dto.PhotographerResponse
import com.damaba.damaba.adapter.inbound.photographer.dto.RegisterPhotographerRequest
import com.damaba.damaba.application.port.inbound.photographer.GetPhotographerUseCase
import com.damaba.damaba.application.port.inbound.photographer.RegisterPhotographerUseCase
import com.damaba.damaba.mapper.PhotographerMapper
import com.damaba.user.domain.user.User
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@Tag(name = "사진작가 관련 API")
@RestController
class PhotographerController(
    private val getPhotographerUseCase: GetPhotographerUseCase,
    private val registerPhotographerUseCase: RegisterPhotographerUseCase,
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
        val photographer = getPhotographerUseCase.getById(photographerId)
        return PhotographerMapper.INSTANCE.toPhotographerResponse(photographer)
    }

    @Operation(
        summary = "사진작가 등록(회원가입)",
        description = "<p>유저 회원가입 시에만 한 번 사용하며, 사진작가 등록 정보(서비스 이용에 필요한 기본 정보)를 받아 설정합니다." +
            "<p>사진작가의 회원가입에만 사용해야 하며, 일반 유저라면 유저 등록 API(<code>PATCH /api/v*/users/me/registration</code>)를 사용해야 합니다.",
        security = [SecurityRequirement(name = "access-token")],
    )
    @ApiResponses(
        ApiResponse(responseCode = "200"),
        ApiResponse(responseCode = "404", description = "유저 정보를 찾을 수 없는 경우", content = [Content()]),
        ApiResponse(
            responseCode = "409",
            description = "<p>이미 등록(가입)된 회원인 경우, 즉 유저 등록 API를 이전헤 호출한 적이 있었던 경우" +
                "<p>수정하고자 하는 닉네임이 이미 사용중인 경우",
            content = [Content()],
        ),
    )
    @PutMapping("/api/v1/photographers/me/registration")
    fun registerPhotographerV1(
        @AuthenticationPrincipal requester: User,
        @RequestBody request: RegisterPhotographerRequest,
    ): PhotographerResponse {
        val photographer = registerPhotographerUseCase.register(request.toCommand(requester.id))
        return PhotographerMapper.INSTANCE.toPhotographerResponse(photographer)
    }
}
