package com.damaba.user.domain.auth

import com.damaba.user.domain.user.constant.LoginType

interface OAuthService {
    fun getOAuthLoginUid(platform: LoginType, authKey: String): String
}
