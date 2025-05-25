package com.damaba.damaba.application.user.dto

import com.damaba.damaba.domain.file.Image
import com.damaba.damaba.domain.user.UserValidator

data class UpdateUserProfileCommand(
    val userId: Long,
    val nickname: String,
    val instagramId: String?,
    val profileImage: Image?,
) {
    init {
        UserValidator.validateNickname(nickname)
        if (instagramId != null) UserValidator.validateInstagramId(instagramId)
    }
}
