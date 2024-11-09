package com.damaba.user.domain.user

import com.damaba.common_exception.ValidationException

object UserValidator {
    private val NICKNAME_PATTERN = "^[a-zA-Zㄱ-ㅎ가-힣0-9]{2,7}$".toRegex()

    fun validateUserNickname(nickname: String) {
        if (nickname.isBlank() || !nickname.matches(NICKNAME_PATTERN)) {
            throw ValidationException("유효하지 않은 닉네임입니다. 닉네임은 한글, 영문, 숫자로 이루어진, 특수문자와 공백을 제외한 2~7 글자여야 합니다.")
        }
    }

    fun validateInstagramId(instagramId: String) {
        if (instagramId.isBlank() || instagramId.length > 30) {
            throw ValidationException("인스타 아이디는 1~30 글자여야 합니다.")
        }
    }
}
