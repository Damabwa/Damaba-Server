package com.damaba.user.domain.user.exception

import com.damaba.damaba.domain.exception.CustomException

class UserNotFoundException :
    CustomException(
        httpStatusCode = 404,
        code = "USER_NOT_FOUND",
        message = "일치하는 유저 정보를 찾을 수 없습니다.",
    )
