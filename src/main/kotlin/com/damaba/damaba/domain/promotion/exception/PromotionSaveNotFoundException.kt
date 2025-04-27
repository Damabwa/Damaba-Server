package com.damaba.damaba.domain.promotion.exception

import com.damaba.damaba.domain.exception.CustomException
import org.springframework.http.HttpStatus

class PromotionSaveNotFoundException :
    CustomException(
        httpStatusCode = HttpStatus.NOT_FOUND.value(),
        code = "PROMOTION_NOT_FOUND",
        message = "프로모션 저장 이력을 찾을 수 없습니다.",
    )
