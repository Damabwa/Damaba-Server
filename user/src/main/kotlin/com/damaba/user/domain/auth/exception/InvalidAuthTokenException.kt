package com.damaba.user.domain.auth.exception

import com.damaba.common_exception.CustomException
import com.damaba.common_exception.CustomExceptionType

class InvalidAuthTokenException(
    message: String? = null,
    cause: Throwable? = null,
) : CustomException(
    httpStatusCode = 401,
    exceptionType = CustomExceptionType.INVALID_AUTH_TOKEN,
    optionalMessage = message,
    cause = cause,
)
