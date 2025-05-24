package com.damaba.damaba.infrastructure.auth

import com.damaba.damaba.domain.auth.AuthToken
import com.damaba.damaba.domain.auth.exception.InvalidAuthTokenException
import com.damaba.damaba.domain.user.User

interface AuthTokenManager {
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

    /**
     * Token에 담긴 user id를 조회한다.
     */
    fun parseUserId(authToken: String): Long

    /**
     * Access token의 유효성을 검증한다.
     *
     * @param authToken 유효성을 확인할 access token(JWT)
     * @throws InvalidAuthTokenException 유효하지 않은 access token인 경우
     */
    fun validateAccessToken(authToken: String)
}
