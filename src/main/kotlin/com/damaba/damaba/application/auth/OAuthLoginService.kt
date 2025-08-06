package com.damaba.damaba.application.auth

import com.damaba.damaba.domain.auth.RefreshToken
import com.damaba.damaba.domain.user.User
import com.damaba.damaba.infrastructure.auth.AuthTokenManager
import com.damaba.damaba.infrastructure.auth.OAuthLoginProvider
import com.damaba.damaba.infrastructure.auth.RefreshTokenRepository
import com.damaba.damaba.infrastructure.user.UserRepository
import com.damaba.damaba.property.AuthProperties
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class OAuthLoginService(
    private val authTokenManager: AuthTokenManager,
    private val oAuthLoginProvider: OAuthLoginProvider,
    private val refreshTokenRepo: RefreshTokenRepository,
    private val userRepo: UserRepository,
    private val authProperties: AuthProperties,
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
    fun oAuthLogin(command: OAuthLoginCommand): OAuthLoginResult {
        val oAuthLoginUid = oAuthLoginProvider.getOAuthLoginUid(command.loginType, command.authKey)

        var isNewUser = false
        var user = userRepo.findByOAuthLoginUid(oAuthLoginUid)
        if (user == null) {
            isNewUser = true
            user = userRepo.create(
                User.create(
                    loginType = command.loginType,
                    oAuthLoginUid = oAuthLoginUid,
                    nickname = generateUniqueNickname(),
                ),
            )
        }

        val accessToken = authTokenManager.createAccessToken(user)
        val refreshToken = authTokenManager.createRefreshToken(user)
        refreshTokenRepo.create(
            refreshToken = RefreshToken(user.id, refreshToken.value),
            ttlMillis = authProperties.refreshTokenDurationMillis,
        )

        return OAuthLoginResult(isNewUser, user, accessToken, refreshToken)
    }

    private fun generateUniqueNickname(): String {
        var nickname: String
        do {
            nickname = UUID.randomUUID().toString().substring(0, 7)
        } while (userRepo.existsNickname(nickname))
        return nickname
    }
}
