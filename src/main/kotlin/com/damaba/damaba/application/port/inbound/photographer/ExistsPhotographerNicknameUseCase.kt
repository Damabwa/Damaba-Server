package com.damaba.damaba.application.port.inbound.photographer

import com.damaba.damaba.domain.photographer.PhotographerValidator

interface ExistsPhotographerNicknameUseCase {
    fun existsNickname(query: Query): Boolean

    data class Query(val nickname: String) {
        init {
            PhotographerValidator.validateNickname(nickname)
        }
    }
}
