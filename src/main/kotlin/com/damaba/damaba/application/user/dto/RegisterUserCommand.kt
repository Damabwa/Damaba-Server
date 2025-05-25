package com.damaba.damaba.application.user.dto

import com.damaba.damaba.domain.user.UserValidator
import com.damaba.damaba.domain.user.constant.Gender

data class RegisterUserCommand(
    val userId: Long,
    val nickname: String,
    val gender: Gender,
    val instagramId: String?,
) {
    init {
        UserValidator.validateNickname(nickname)
        if (instagramId != null) UserValidator.validateInstagramId(instagramId)
    }
}
