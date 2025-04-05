package com.damaba.damaba.domain.promotion.exception

import com.damaba.damaba.domain.exception.CustomException

class PromotionDeletePermissionDeniedException :
    CustomException(
        httpStatusCode = 403,
        code = "PROMOTION_DELETE_PERMISSION_DENIED",
        message = "프로모션을 삭제할 수 있는 권한이 없습니다.",
    )
