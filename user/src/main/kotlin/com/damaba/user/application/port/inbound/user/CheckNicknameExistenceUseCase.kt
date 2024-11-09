package com.damaba.user.application.port.inbound.user

import com.damaba.user.domain.user.UserValidator

interface CheckNicknameExistenceUseCase {
    fun doesNicknameExist(query: Query): Boolean

    data class Query(val nickname: String) {
        init {
            UserValidator.validateUserNickname(nickname)
        }
    }
}
