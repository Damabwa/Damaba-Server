package com.damaba.user.application.port.outbound.auth

import com.damaba.user.domain.auth.AuthToken
import com.damaba.user.domain.user.User

interface CreateAuthTokenPort {
    /**
     * Access token(JWT)을 발행한다.
     *
     * @param user access token에 담을 유저 정보
     * @return 생성된 access token 정보
     */
    fun createAccessToken(user: User): AuthToken

    /**
     * Refresh token(JWT)을 발행한다.
     * 생성된 refresh token은 DB에 저장된다.
     *
     * @param user refresh token에 담을 유저 정보
     * @return 생성된 refresh token 정보
     */
    fun createRefreshToken(user: User): AuthToken
}
