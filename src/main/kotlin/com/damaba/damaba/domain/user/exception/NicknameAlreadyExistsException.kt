package com.damaba.user.domain.user.exception

import com.damaba.damaba.domain.exception.CustomException

class NicknameAlreadyExistsException(nickname: String) :
    CustomException(
        httpStatusCode = 409,
        code = "NICKNAME_ALREADY_EXISTS",
        message = "이미 사용중인 닉네임입니다. Nickname=$nickname",
    )
