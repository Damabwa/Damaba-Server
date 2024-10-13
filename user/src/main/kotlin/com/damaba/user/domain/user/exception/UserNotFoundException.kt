package com.damaba.user.domain.user.exception

import com.damaba.common_exception.CustomException
import com.damaba.common_exception.CustomExceptionType

class UserNotFoundException :
    CustomException(
        httpStatusCode = 404,
        exceptionType = CustomExceptionType.USER_NOT_FOUND,
    )
