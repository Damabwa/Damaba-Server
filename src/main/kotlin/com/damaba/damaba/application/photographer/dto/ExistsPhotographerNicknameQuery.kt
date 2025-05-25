package com.damaba.damaba.application.photographer.dto

import com.damaba.damaba.domain.photographer.PhotographerValidator

data class ExistsPhotographerNicknameQuery(val nickname: String) {
    init {
        PhotographerValidator.validateNickname(nickname)
    }
}
