package com.damaba.user.domain.user.exception

import com.damaba.damaba.domain.exception.CustomException

class UserAlreadyRegisteredException :
    CustomException(
        httpStatusCode = 409,
        code = "USER_ALREADY_REGISTERED",
        message = "이미 등록(가입)된 유저입니다.",
    )
