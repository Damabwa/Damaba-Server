package com.damaba.user.adapter.inbound.user

import com.damaba.user.adapter.inbound.user.dto.CheckNicknameExistenceResponse
import com.damaba.user.adapter.inbound.user.dto.UpdateMyInfoRequest
import com.damaba.user.adapter.inbound.user.dto.UserResponse
import com.damaba.user.application.port.inbound.user.CheckNicknameExistenceUseCase
import com.damaba.user.application.port.inbound.user.GetMyInfoUseCase
import com.damaba.user.application.port.inbound.user.UpdateMyInfoUseCase
import com.damaba.user.domain.user.User
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "유저 관련 API")
@RestController
class UserController(
    private val getMyInfoUseCase: GetMyInfoUseCase,
    private val checkNicknameExistenceUseCase: CheckNicknameExistenceUseCase,
    private val updateMyInfoUseCase: UpdateMyInfoUseCase,
) {
    @Operation(
        summary = "내 정보 조회",
        description = "내 정보를 조회합니다.",
        security = [SecurityRequirement(name = "access-token")],
    )
    @GetMapping("/api/v1/users/me")
    fun getMyInfoV1(@AuthenticationPrincipal requestUser: User): UserResponse {
        val me = getMyInfoUseCase.getMyInfo(requestUser.id)
        return UserResponse.from(me)
    }

    @Operation(
        summary = "닉네임 사용여부 확인",
        description = "사용중인 닉네임인지 확인합니다.",
    )
    @GetMapping("/api/v1/users/nicknames/existence")
    fun checkNicknameExistenceV1(
        @Parameter(
            description = "사용중인지 확인할 닉네임",
            example = "치와와",
        ) @RequestParam nickname: String,
    ): CheckNicknameExistenceResponse {
        val doesNicknameExists =
            checkNicknameExistenceUseCase.doesNicknameExist(CheckNicknameExistenceUseCase.Query(nickname))
        return CheckNicknameExistenceResponse(nickname, doesNicknameExists)
    }

    @Operation(
        summary = "내 정보 수정",
        description = "<p>내 정보를 수정합니다. 요청된 정보들로 기존 유저 정보를 변경(overwrite)합니다." +
            "<p>정보가 변경되지 않은 항목이더라도 요청 데이터에 담아야 합니다. 그 때문에 변경되지 않은 항목은 기존 값을 담아 요청해야 합니다." +
            "<p>예외적으로, 프로필 이미지(`profileImage`)는 변경하고자 할 때에만 담아 요청하면 됩니다.",
        security = [SecurityRequirement(name = "access-token")],
    )
    @ApiResponses(
        ApiResponse(responseCode = "200"),
        ApiResponse(responseCode = "404", description = "유저 정보를 찾을 수 없는 경우", content = [Content()]),
        ApiResponse(responseCode = "409", description = "수정하고자 하는 닉네임이 이미 사용중인 경우", content = [Content()]),
    )
    @PutMapping("/api/v1/users/me")
    fun updateMyInfoV1(
        @AuthenticationPrincipal requestUser: User,
        @RequestBody request: UpdateMyInfoRequest,
    ): UserResponse {
        val updatedUser = updateMyInfoUseCase.updateMyInfo(request.toCommand(requestUserId = requestUser.id))
        return UserResponse.from(updatedUser)
    }
}
