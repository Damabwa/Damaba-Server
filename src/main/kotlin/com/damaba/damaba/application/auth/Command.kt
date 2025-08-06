@file:Suppress("ktlint:standard:filename")

package com.damaba.damaba.application.auth

import com.damaba.damaba.domain.exception.ValidationException
import com.damaba.damaba.domain.user.constant.LoginType

data class OAuthLoginCommand(
    val loginType: LoginType,
    val authKey: String,
) {
    init {
        if (authKey.isBlank()) {
            throw ValidationException("Auth key는 공백일 수 없습니다.")
        }
    }
}
