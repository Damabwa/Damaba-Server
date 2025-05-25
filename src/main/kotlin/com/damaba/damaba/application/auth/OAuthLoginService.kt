package com.damaba.damaba.application.auth

import com.damaba.damaba.application.port.inbound.auth.OAuthLoginUseCase
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
) : OAuthLoginUseCase {

    @Transactional
    override fun oAuthLogin(command: OAuthLoginUseCase.Command): OAuthLoginUseCase.Result {
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

        return OAuthLoginUseCase.Result(isNewUser, user, accessToken, refreshToken)
    }

    private fun generateUniqueNickname(): String {
        var nickname: String
        do {
            nickname = UUID.randomUUID().toString().substring(0, 7)
        } while (userRepo.existsNickname(nickname))
        return nickname
    }
}
