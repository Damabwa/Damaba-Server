package com.damaba.user.application.port.inbound.auth

import com.damaba.common_exception.ValidationException
import com.damaba.user.domain.auth.AuthToken
import com.damaba.user.domain.user.User
import com.damaba.user.domain.user.constant.LoginType
import org.springframework.transaction.annotation.Transactional

interface OAuthLoginUseCase {
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
    fun oAuthLogin(command: Command): Result

    data class Command(
        val loginType: LoginType,
        val authKey: String,
    ) {
        init {
            if (authKey.isBlank()) {
                throw ValidationException("Auth key는 공백일 수 없습니다.")
            }
        }
    }

    data class Result(
        val isNewUser: Boolean,
        val user: User,
        val accessToken: AuthToken,
        val refreshToken: AuthToken,
    )
}
