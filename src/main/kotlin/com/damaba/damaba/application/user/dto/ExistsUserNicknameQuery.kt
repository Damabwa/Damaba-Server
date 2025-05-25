package com.damaba.damaba.application.user.dto

import com.damaba.damaba.domain.user.UserValidator

data class ExistsUserNicknameQuery(val nickname: String) {
    init {
        UserValidator.validateNickname(nickname)
    }
}
