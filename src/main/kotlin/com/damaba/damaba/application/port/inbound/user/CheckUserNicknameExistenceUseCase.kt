package com.damaba.damaba.application.port.inbound.user

import com.damaba.user.domain.user.UserValidator

interface CheckUserNicknameExistenceUseCase {
    fun doesNicknameExist(query: Query): Boolean

    data class Query(val nickname: String) {
        init {
            UserValidator.validateNickname(nickname)
        }
    }
}
