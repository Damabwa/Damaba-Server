package com.damaba.user.application.port.inbound.user

import com.damaba.user.domain.user.User
import com.damaba.user.domain.user.UserProfileImage
import com.damaba.user.domain.user.UserValidator

interface UpdateUserUseCase {
    fun updateUser(command: Command): User

    data class Command(
        val userId: Long,
        val nickname: String,
        val instagramId: String?,
        val profileImage: UserProfileImage,
    ) {
        init {
            UserValidator.validateUserNickname(nickname)
            if (instagramId != null) UserValidator.validateInstagramId(instagramId)
        }
    }
}
