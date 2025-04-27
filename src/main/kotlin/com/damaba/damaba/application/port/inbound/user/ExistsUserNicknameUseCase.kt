package com.damaba.damaba.application.port.inbound.user

import com.damaba.damaba.domain.user.UserValidator

interface ExistsUserNicknameUseCase {
    fun existsNickname(query: Query): Boolean

    data class Query(val nickname: String) {
        init {
            UserValidator.validateNickname(nickname)
        }
    }
}
