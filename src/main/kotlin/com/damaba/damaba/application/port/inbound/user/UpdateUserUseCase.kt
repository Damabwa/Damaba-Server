package com.damaba.damaba.application.port.inbound.user

import com.damaba.damaba.domain.file.Image
import com.damaba.damaba.domain.user.User
import com.damaba.damaba.domain.user.UserValidator

interface UpdateUserUseCase {
    fun updateUser(command: Command): User

    data class Command(
        val userId: Long,
        val nickname: String,
        val instagramId: String?,
        val profileImage: Image,
    ) {
        init {
            UserValidator.validateNickname(nickname)
            if (instagramId != null) UserValidator.validateInstagramId(instagramId)
        }
    }
}
