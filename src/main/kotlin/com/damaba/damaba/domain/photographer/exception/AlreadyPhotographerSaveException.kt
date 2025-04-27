package com.damaba.damaba.domain.photographer.exception

import com.damaba.damaba.domain.exception.CustomException
import org.springframework.http.HttpStatus

class AlreadyPhotographerSaveException :
    CustomException(
        httpStatusCode = HttpStatus.CONFLICT.value(),
        code = "ALREADY_PHOTOGRAPHER_SAVE",
        message = "이미 저장한 사진작가입니다.",
    )
