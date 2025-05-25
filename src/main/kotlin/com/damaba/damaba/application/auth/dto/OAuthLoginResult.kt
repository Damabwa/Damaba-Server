package com.damaba.damaba.application.auth.dto

import com.damaba.damaba.domain.auth.AuthToken
import com.damaba.damaba.domain.user.User

data class OAuthLoginResult(
    val isNewUser: Boolean,
    val user: User,
    val accessToken: AuthToken,
    val refreshToken: AuthToken,
)
