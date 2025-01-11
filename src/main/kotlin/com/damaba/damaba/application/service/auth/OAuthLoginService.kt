package com.damaba.damaba.application.service.auth

import com.damaba.damaba.application.port.inbound.auth.OAuthLoginUseCase
import com.damaba.damaba.application.port.outbound.auth.CreateAuthTokenPort
import com.damaba.damaba.application.port.outbound.auth.CreateRefreshTokenPort
import com.damaba.damaba.application.port.outbound.auth.GetOAuthLoginUidPort
import com.damaba.damaba.application.port.outbound.user.CheckNicknameExistencePort
import com.damaba.damaba.application.port.outbound.user.CreateUserPort
import com.damaba.damaba.application.port.outbound.user.FindUserPort
import com.damaba.damaba.domain.auth.RefreshToken
import com.damaba.damaba.domain.user.User
import com.damaba.damaba.property.AuthProperties
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class OAuthLoginService(
    private val getOAuthLoginUidPort: GetOAuthLoginUidPort,
    private val findUserPort: FindUserPort,
    private val checkNicknameExistencePort: CheckNicknameExistencePort,
    private val createUserPort: CreateUserPort,
    private val createAuthTokenPort: CreateAuthTokenPort,
    private val createRefreshTokenPort: CreateRefreshTokenPort,
    private val authProperties: AuthProperties,
) : OAuthLoginUseCase {

    @Transactional
    override fun oAuthLogin(command: OAuthLoginUseCase.Command): OAuthLoginUseCase.Result {
        val oAuthLoginUid = getOAuthLoginUidPort.getOAuthLoginUid(command.loginType, command.authKey)

        var isNewUser = false
        var user = findUserPort.findByOAuthLoginUid(oAuthLoginUid)
        if (user == null) {
            isNewUser = true
            user = createUserPort.create(
                User.create(
                    loginType = command.loginType,
                    oAuthLoginUid = oAuthLoginUid,
                    nickname = generateUniqueNickname(),
                ),
            )
        }

        val accessToken = createAuthTokenPort.createAccessToken(user)
        val refreshToken = createAuthTokenPort.createRefreshToken(user)
        createRefreshTokenPort.create(
            refreshToken = RefreshToken(user.id, refreshToken.value),
            ttlMillis = authProperties.refreshTokenDurationMillis,
        )

        return OAuthLoginUseCase.Result(isNewUser, user, accessToken, refreshToken)
    }

    private fun generateUniqueNickname(): String {
        var nickname: String
        do {
            nickname = UUID.randomUUID().toString().substring(0, 7)
        } while (checkNicknameExistencePort.doesNicknameExist(nickname))
        return nickname
    }
}
