package com.damaba.damaba.domain.photographer.exception

import com.damaba.damaba.domain.exception.CustomException
import org.springframework.http.HttpStatus

class SavedPhotographerNotFoundException :
    CustomException(
        httpStatusCode = HttpStatus.NOT_FOUND.value(),
        code = "SAVED_PHOTOGRAPHER_NOT_FOUND",
        message = "사진작가 저장 이력을 찾을 수 없습니다.",
    )
