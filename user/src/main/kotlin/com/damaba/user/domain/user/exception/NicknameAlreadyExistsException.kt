package com.damaba.user.domain.user.exception

import com.damaba.common_exception.CustomException
import com.damaba.common_exception.CustomExceptionType

class NicknameAlreadyExistsException(nickname: String) :
    CustomException(
        httpStatusCode = 409,
        exceptionType = CustomExceptionType.NICKNAME_ALREADY_EXISTS,
        optionalMessage = "Nickname=$nickname",
    )
