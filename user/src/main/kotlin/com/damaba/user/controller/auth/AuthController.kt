package com.damaba.user.controller.auth

import com.damaba.user.application.auth.OAuthLoginUseCase
import com.damaba.user.controller.auth.dto.OAuthLoginRequest
import com.damaba.user.controller.auth.dto.OAuthLoginResponse
import com.damaba.user.domain.user.constant.LoginType
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@Tag(name = "인증 관련 API")
@RestController
class AuthController(private val oAuthLoginUseCase: OAuthLoginUseCase) {
    @Operation(
        summary = "OAuth 로그인",
        description = "<p>로그인을 수행합니다. 만약 신규 유저라면, 신규 유저 데이터를 생성 및 저장합니다." +
            "<p>이후 로그인 결과로 로그인한 유저 정보와 auth tokens를 반환합니다.",
    )
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "OK. 기존 유저가 로그인한 경우"),
        ApiResponse(responseCode = "201", description = "Created. 신규 유저가 로그인하여, 회원가입이 진행된 경우"),
        ApiResponse(responseCode = "400", description = "[KKA_0001] Kakao API 서버로부터 4XX 에러를 응답받은 경우.", content = [Content()]),
    )
    @PostMapping("/api/v1/auth/login")
    fun oAuthLoginV1(@RequestBody @Valid request: OAuthLoginRequest): ResponseEntity<OAuthLoginResponse> {
        val (isNewUser, user, accessToken, refreshToken) =
            oAuthLoginUseCase(OAuthLoginUseCase.Command(loginType = LoginType.KAKAO, authKey = request.authKey))

        return if (isNewUser) {
            ResponseEntity
                .created(URI.create("/api/v*/users/${user.id}"))
                .body(OAuthLoginResponse.from(user, accessToken, refreshToken))
        } else {
            ResponseEntity
                .ok()
                .body(OAuthLoginResponse.from(user, accessToken, refreshToken))
        }
    }
}