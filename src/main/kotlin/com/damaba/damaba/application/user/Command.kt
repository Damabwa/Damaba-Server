package com.damaba.damaba.application.user

import com.damaba.damaba.application.term.TermItem
import com.damaba.damaba.domain.file.Image
import com.damaba.damaba.domain.term.TermValidator
import com.damaba.damaba.domain.user.UserValidator
import com.damaba.damaba.domain.user.constant.Gender

data class RegisterUserCommand(
    val userId: Long,
    val nickname: String,
    val gender: Gender,
    val instagramId: String?,
    val terms: List<TermItem>,
) {
    init {
        UserValidator.validateNickname(nickname)
        if (instagramId != null) UserValidator.validateInstagramId(instagramId)
        TermValidator.validateUserRequired(terms)
    }
}

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
