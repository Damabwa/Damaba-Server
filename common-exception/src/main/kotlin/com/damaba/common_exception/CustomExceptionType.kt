package com.damaba.common_exception

enum class CustomExceptionType(
    val code: String,
    val message: String,
) {
    KAKAO_API("KKA_0001", "카카오 서버와의 통신에서 에러가 발생했습니다. 요청 데이터가 잘못되지는 않았는지 다시 한 번 확인해주세요."),
}
