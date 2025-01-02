package com.damaba.damaba.domain.promotion.exception

import com.damaba.damaba.domain.exception.CustomException

class PromotionNotFoundException :
    CustomException(
        httpStatusCode = 404,
        code = "PROMOTION_NOT_FOUND",
        message = "일치하는 프로모션 정보를 찾을 수 없습니다.",
    )
