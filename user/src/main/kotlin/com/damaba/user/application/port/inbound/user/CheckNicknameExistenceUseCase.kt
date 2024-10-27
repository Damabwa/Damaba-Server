package com.damaba.user.application.port.inbound.user

import com.damaba.user.domain.user.UserValidator

interface CheckNicknameExistenceUseCase {
    fun doesNicknameExist(command: Command): Boolean

    data class Command(val nickname: String) {
        init {
            UserValidator.validateNickname(nickname)
        }
    }
}
