package com.damaba.user.application.port.outbound.auth

import com.damaba.user.domain.auth.exception.InvalidAuthTokenException

interface ValidateAuthTokenPort {
    /**
     * Token의 유효성을 검증한다.
     *
     * @param authToken 유효성을 확인할 token(JWT)
     * @throws InvalidAuthTokenException 유효하지 않은 token인 경우
     */
    fun validate(authToken: String)
}
