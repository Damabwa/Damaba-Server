package com.damaba.user.domain.auth.exception

import com.damaba.common_exception.CustomException

class InvalidAuthTokenException(
    optionalMessage: String? = null,
    cause: Throwable? = null,
) : CustomException(
    httpStatusCode = 401,
    code = "INVALID_AUTH_TOKEN",
    message = "유효하지 않은 인증 토큰입니다. 토큰 값이 잘못되었거나 만료되어 유효하지 않은 경우로 token 갱신이 필요합니다. $optionalMessage",
    cause = cause,
)
