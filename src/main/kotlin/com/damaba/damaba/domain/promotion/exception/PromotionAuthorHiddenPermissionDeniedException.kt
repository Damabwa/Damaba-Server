package com.damaba.damaba.domain.promotion.exception

import com.damaba.damaba.domain.exception.CustomException

class PromotionAuthorHiddenPermissionDeniedException :
    CustomException(
        httpStatusCode = 403,
        code = "PROMOTION_AUTHOR_HIDDEN_PERMISSION_DENIED",
        message = "작성자 정보 숨김은 관리자만 설정할 수 있습니다.",
    )
