package com.damaba.damaba.domain.photographer.exception

import com.damaba.damaba.domain.exception.CustomException
import org.springframework.http.HttpStatus

class AlreadySavedPhotographerException :
    CustomException(
        httpStatusCode = HttpStatus.CONFLICT.value(),
        code = "ALREADY_SAVED_PHOTOGRAPHER",
        message = "이미 저장한 사진작가입니다.",
    )
