package com.damaba.damaba.domain.promotion.exception

import com.damaba.damaba.domain.exception.CustomException
import org.springframework.http.HttpStatus

class AlreadyPromotionSaveException :
    CustomException(
        httpStatusCode = HttpStatus.CONFLICT.value(),
        code = "ALREADY_PROMOTION_SAVE",
        message = "이미 저장된 프로모션입니다.",
    )
