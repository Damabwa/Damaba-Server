package com.damaba.user.controller.user

import com.damaba.user.application.user.CheckNicknameAvailabilityUseCase
import com.damaba.user.application.user.GetMyInfoUseCase
import com.damaba.user.application.user.UpdateMyInfoUseCase
import com.damaba.user.controller.user.dto.CheckNicknameAvailabilityResponse
import com.damaba.user.controller.user.dto.UpdateMyInfoRequest
import com.damaba.user.controller.user.dto.UserResponse
import com.damaba.user.domain.user.User
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "유저 관련 API")
@RestController
class UserController(
    private val getMyInfo: GetMyInfoUseCase,
    private val checkNicknameAvailability: CheckNicknameAvailabilityUseCase,
    private val updateMyInfo: UpdateMyInfoUseCase,
) {
    @Operation(
        summary = "내 정보 조회",
        description = "내 정보를 조회합니다.",
        security = [SecurityRequirement(name = "access-token")],
    )
    @GetMapping("/api/v1/users/me")
    fun getMyInfoV1(@AuthenticationPrincipal requestUser: User): UserResponse {
        val me = getMyInfo(requestUser.id)
        return UserResponse.from(me)
    }

    @Operation(
        summary = "닉네임 이용가능성 확인",
        description = "이용 가능한 닉네임인지 확인합니다.",
    )
    @GetMapping("/api/v1/users/nicknames/availability")
    fun checkNicknameAvailabilityV1(@RequestParam nickname: String): CheckNicknameAvailabilityResponse {
        val availability = checkNicknameAvailability(nickname)
        return CheckNicknameAvailabilityResponse(nickname, availability)
    }

    @Operation(
        summary = "내 정보 수정",
        description = "내 정보를 수정합니다. 요청 시, 수정하고자 하는 정보들만 전달하면 됩니다.",
        security = [SecurityRequirement(name = "access-token")],
    )
    @ApiResponses(
        ApiResponse(responseCode = "200"),
        ApiResponse(responseCode = "404", description = "[USR_0100] 유저 정보를 찾을 수 없는 경우", content = [Content()]),
        ApiResponse(responseCode = "409", description = "[USR_0101] 수정하고자 하는 닉네임이 이미 사용중인 경우", content = [Content()]),
    )
    @PatchMapping("/api/v1/users/me", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun updateMyInfoV1(
        @AuthenticationPrincipal requestUser: User,
        @ModelAttribute @Valid request: UpdateMyInfoRequest,
    ): UserResponse {
        val updatedUser = updateMyInfo(request.toCommand(requestUserId = requestUser.id))
        return UserResponse.from(updatedUser)
    }
}
