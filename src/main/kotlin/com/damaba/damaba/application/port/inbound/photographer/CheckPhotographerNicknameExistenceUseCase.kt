package com.damaba.damaba.application.port.inbound.photographer

import com.damaba.damaba.domain.photographer.PhotographerValidator

interface CheckPhotographerNicknameExistenceUseCase {
    fun doesNicknameExist(query: Query): Boolean

    data class Query(val nickname: String) {
        init {
            PhotographerValidator.validateNickname(nickname)
        }
    }
}
