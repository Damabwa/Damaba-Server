package com.damaba.user.application.port.inbound.user

import com.damaba.user.domain.user.User
import com.damaba.user.domain.user.UserProfileImage
import com.damaba.user.domain.user.UserValidator
import com.damaba.user.domain.user.constant.Gender

interface UpdateMyInfoUseCase {
    fun updateMyInfo(command: Command): User

    data class Command(
        val userId: Long,
        val nickname: String,
        val gender: Gender,
        val instagramId: String?,
        val profileImage: UserProfileImage,
    ) {
        init {
            UserValidator.validateUserNickname(nickname)
            if (instagramId != null) UserValidator.validateInstagramId(instagramId)
        }
    }
}
