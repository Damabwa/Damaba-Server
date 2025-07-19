@file:Suppress("ktlint:standard:filename")

package com.damaba.damaba.application.user

import com.damaba.damaba.domain.user.UserValidator

data class ExistsUserNicknameQuery(val nickname: String) {
    init {
        UserValidator.validateNickname(nickname)
    }
}
