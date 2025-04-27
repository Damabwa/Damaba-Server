package com.damaba.damaba.application.port.outbound.auth

import com.damaba.damaba.domain.auth.exception.InvalidAuthTokenException

interface ValidateAuthTokenPort {
    /**
     * Access token의 유효성을 검증한다.
     *
     * @param authToken 유효성을 확인할 access token(JWT)
     * @throws InvalidAuthTokenException 유효하지 않은 access token인 경우
     */
    fun validateAccessToken(authToken: String)
}
