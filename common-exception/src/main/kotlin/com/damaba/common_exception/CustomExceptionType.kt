package com.damaba.common_exception

enum class CustomExceptionType(
    val code: String,
    val message: String,
) {
    INVALID_AUTH_CREDENTIALS("COM_0001", "유효하지 않은 자격 증명입니다. 인증/인가를 위한 토큰이 잘못되지는 않았는지 확인해주세요."),
    ACCESS_DENIED("COM_0002", "접근이 거부되었습니다. 접근을 위한 권한을 확인해주세요."),

    // Auth
    INVALID_AUTH_TOKEN("USR_0000", "유효하지 않은 토큰입니다. 토큰 값이 잘못되었거나 만료되어 유효하지 않은 경우로 token 갱신이 필요합니다."),

    // User
    USER_NOT_FOUND("USR_0100", "일치하는 유저 정보를 찾을 수 없습니다."),
    NICKNAME_ALREADY_EXISTS("USR_0101", "이미 사용중인 닉네임입니다."),

    // Kakao
    KAKAO_API("KKA_0000", "카카오 서버와의 통신에서 에러가 발생했습니다. 요청 데이터가 잘못되지는 않았는지 다시 한 번 확인해주세요."),
}
