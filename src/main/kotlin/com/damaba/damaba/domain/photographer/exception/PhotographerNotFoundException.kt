package com.damaba.damaba.domain.photographer.exception

import com.damaba.damaba.domain.exception.CustomException

class PhotographerNotFoundException :
    CustomException(
        httpStatusCode = 404,
        code = "PHOTOGRAPHER_NOT_FOUND",
        message = "일치하는 사진작가를 찾을 수 없습니다",
    )
