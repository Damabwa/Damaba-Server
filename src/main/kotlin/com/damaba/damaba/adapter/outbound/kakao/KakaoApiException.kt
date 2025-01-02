package com.damaba.damaba.adapter.outbound.kakao

import com.damaba.damaba.domain.exception.CustomException

class KakaoApiException(code: Int, msg: String) :
    CustomException(
        httpStatusCode = 400,
        code = "KAKAO_API",
        message = "카카오 서버와의 통신에서 에러가 발생했습니다. 요청 데이터가 잘못되지는 않았는지 다시 한 번 확인해주세요. Error info=(code='$code', msg='$msg')",
    )
