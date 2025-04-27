package com.damaba.damaba.domain.promotion.exception

import com.damaba.damaba.domain.exception.CustomException

class PromotionUpdatePermissionDeniedException :
    CustomException(
        httpStatusCode = 403,
        code = "PROMOTION_UPDATE_PERMISSION_DENIED",
        message = "프로모션을 수정할 수 있는 권한이 없습니다.",
    )
