package com.damaba.user.infrastructure.kakao

import com.damaba.common_exception.CustomException
import com.damaba.common_exception.CustomExceptionType

class KakaoApiException(code: Int, msg: String) :
    CustomException(
        httpStatusCode = 400,
        exceptionType = CustomExceptionType.KAKAO_API,
        optionalMessage = "Kakao error info=(code='$code', msg='$msg')",
    )
