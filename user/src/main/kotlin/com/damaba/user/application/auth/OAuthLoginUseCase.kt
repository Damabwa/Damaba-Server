package com.damaba.user.application.auth

import com.damaba.user.domain.auth.AuthToken
import com.damaba.user.domain.auth.AuthTokenService
import com.damaba.user.domain.auth.OAuthService
import com.damaba.user.domain.user.User
import com.damaba.user.domain.user.UserService
import com.damaba.user.domain.user.constant.LoginType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OAuthLoginUseCase(
    private val oAuthService: OAuthService,
    private val authTokenService: AuthTokenService,
    private val userService: UserService,
) {
    /**
     * OAuth(Kakao 로그인.
     *
     * OAuth login uid를 조회한 후, 다음 로직을 수행한다.
     * - 신규 유저라면: 유저 데이터 생성 및 저장
     * - 기존 유저라면: 유저 데이터 조회
     *
     * 이후 로그인한 유저 정보로 access token과 refresh token을 생성하여 반환한다.
     *
     * @param command
     * @return 신규 유저 데이터 생성 여부, 신규 유저인지에 대한 정보, access token 정보, refresh token 정보 반환
     */
    @Transactional
    operator fun invoke(command: Command): Result {
        val oAuthLoginUid = oAuthService.getOAuthLoginUid(command.loginType, command.authKey)

        var isNewUser = false
        var user = userService.findUserByOAuthLoginUid(oAuthLoginUid)
        if (user == null) {
            isNewUser = true
            user = userService.createNewUser(oAuthLoginUid, command.loginType)
        }

        val accessToken = authTokenService.createAccessToken(user)
        val refreshToken = authTokenService.createRefreshToken(user)

        return Result(isNewUser, user, accessToken, refreshToken)
    }

    data class Command(
        val loginType: LoginType,
        val authKey: String,
    )

    data class Result(
        val isNewUser: Boolean,
        val user: User,
        val accessToken: AuthToken,
        val refreshToken: AuthToken,
    )
}
