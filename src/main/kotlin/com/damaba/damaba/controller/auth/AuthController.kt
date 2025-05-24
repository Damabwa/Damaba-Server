package com.damaba.damaba.controller.auth

import com.damaba.damaba.application.port.inbound.auth.OAuthLoginUseCase
import com.damaba.damaba.controller.auth.request.OAuthLoginRequest
import com.damaba.damaba.controller.auth.response.OAuthLoginResponse
import com.damaba.damaba.domain.user.constant.LoginType
import com.damaba.damaba.mapper.AuthTokenMapper
import com.damaba.damaba.mapper.UserMapper
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
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
        ApiResponse(responseCode = "400", description = "Kakao API 서버로부터 4XX 에러를 응답받은 경우.", content = [Content()]),
    )
    @PostMapping("/api/v1/auth/login")
    fun oAuthLoginV1(@RequestBody request: OAuthLoginRequest): ResponseEntity<OAuthLoginResponse> {
        val (isNewUser, user, accessToken, refreshToken) = oAuthLoginUseCase.oAuthLogin(
            OAuthLoginUseCase.Command(loginType = LoginType.KAKAO, authKey = request.authKey),
        )

        val response = OAuthLoginResponse(
            isRegistrationCompleted = user.isRegistrationCompleted,
            user = UserMapper.INSTANCE.toUserResponse(user),
            accessToken = AuthTokenMapper.INSTANCE.toAuthTokenResponse(accessToken),
            refreshToken = AuthTokenMapper.INSTANCE.toAuthTokenResponse(refreshToken),
        )
        return if (isNewUser) {
            ResponseEntity
                .created(URI.create("/api/v*/users/${user.id}"))
                .body(response)
        } else {
            ResponseEntity
                .ok()
                .body(response)
        }
    }
}
