package com.damaba.damaba.controller.user

import com.damaba.damaba.application.photographer.PhotographerService
import com.damaba.damaba.application.user.ExistsUserNicknameQuery
import com.damaba.damaba.application.user.UserService
import com.damaba.damaba.controller.user.dto.ExistsUserNicknameResponse
import com.damaba.damaba.controller.user.dto.RegisterUserRequest
import com.damaba.damaba.controller.user.dto.UpdateMyProfileRequest
import com.damaba.damaba.controller.user.dto.UserResponse
import com.damaba.damaba.domain.user.User
import com.damaba.damaba.domain.user.constant.UserType
import com.damaba.damaba.mapper.UserMapper
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "유저 관련 API")
@RestController
class UserController(
    private val userService: UserService,
    private val photographerService: PhotographerService,
) {
    @Operation(
        summary = "내 정보 조회",
        description = "내 정보를 조회합니다.",
        security = [SecurityRequirement(name = "access-token")],
    )
    @GetMapping("/api/v1/users/me")
    fun getMyInfoV1(@AuthenticationPrincipal requestUser: User): UserResponse {
        val me = userService.getUser(requestUser.id)
        return UserMapper.INSTANCE.toUserResponse(me)
    }

    @Operation(
        summary = "닉네임 사용여부 확인",
        description = "사용중인 닉네임인지 확인합니다.",
    )
    @GetMapping("/api/v1/users/nicknames/existence")
    fun existsUserNicknameV1(
        @Parameter(
            description = "사용중인지 확인할 닉네임",
            example = "치와와",
        ) @RequestParam nickname: String,
    ): ExistsUserNicknameResponse {
        val doesNicknameExists = userService.existsNickname(ExistsUserNicknameQuery(nickname))
        return ExistsUserNicknameResponse(nickname, doesNicknameExists)
    }

    @Operation(
        summary = "유저 등록(회원가입)",
        description = "<p>유저 회원가입 시에만 한 번 사용하며, 유저 등록 정보(서비스 이용에 필요한 기본 정보)를 받아 설정합니다." +
            "<p>일반 유저의 회원가입에만 사용해야 하며, 사진작가라면 사진작가 등록 API(<code>POST /api/v*/photographers/me/registration</code>)를 사용해야 합니다.",
        security = [SecurityRequirement(name = "access-token")],
    )
    @ApiResponses(
        ApiResponse(responseCode = "200"),
        ApiResponse(responseCode = "404", description = "유저 정보를 찾을 수 없는 경우", content = [Content()]),
        ApiResponse(
            responseCode = "409",
            description = "<p>이미 등록(가입)된 유저인 경우, 즉 유저 등록 API를 이전헤 호출한 적이 있었던 경우" +
                "<p>수정하고자 하는 닉네임이 이미 사용중인 경우",
            content = [Content()],
        ),
    )
    @PostMapping("/api/v1/users/me/registration")
    fun registerUserV1(
        @AuthenticationPrincipal requester: User,
        @RequestBody request: RegisterUserRequest,
    ): UserResponse {
        val user = userService.register(request.toCommand(requester.id))
        return UserMapper.INSTANCE.toUserResponse(user)
    }

    @Operation(
        summary = "내 프로필 수정",
        description = "<p>내 프로필을 수정합니다. 요청된 정보들로 기존 유저 정보를 변경(overwrite)합니다." +
            "<p>정보가 변경되지 않은 항목이더라도 요청 데이터에 모두 담아야 합니다. 그 때문에 변경되지 않은 항목은 기존 값을 그대로 담아 요청해야 합니다.",
        security = [SecurityRequirement(name = "access-token")],
    )
    @ApiResponses(
        ApiResponse(responseCode = "200"),
        ApiResponse(responseCode = "404", description = "유저 정보를 찾을 수 없는 경우", content = [Content()]),
        ApiResponse(responseCode = "409", description = "수정하고자 하는 닉네임이 이미 사용중인 경우", content = [Content()]),
    )
    @PutMapping("/api/v1/users/me/profile")
    fun updateMyProfileV1(
        @AuthenticationPrincipal requestUser: User,
        @RequestBody request: UpdateMyProfileRequest,
    ): UserResponse {
        val updatedUser = userService.updateUserProfile(request.toCommand(requestUserId = requestUser.id))
        return UserMapper.INSTANCE.toUserResponse(updatedUser)
    }

    @Operation(
        summary = "회원 탈퇴",
        description = "회원 탈퇴합니다.",
        security = [SecurityRequirement(name = "access-token")],
    )
    @ApiResponses(
        ApiResponse(responseCode = "204"),
        ApiResponse(responseCode = "404", description = "유저를 찾을 수 없는 경우", content = [Content()]),
    )
    @DeleteMapping("/api/v1/users/me")
    fun deleteMeV1(@AuthenticationPrincipal requestUser: User) {
        val user = userService.getUser(requestUser.id)
        when (user.type) {
            UserType.USER, UserType.UNDEFINED -> userService.deleteUser(requestUser.id)
            UserType.PHOTOGRAPHER -> photographerService.deletePhotographer(requestUser.id)
        }
    }
}
