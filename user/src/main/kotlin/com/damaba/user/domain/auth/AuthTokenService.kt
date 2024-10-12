package com.damaba.user.domain.auth

import com.damaba.user.domain.user.User

interface AuthTokenService {
    fun createAccessToken(user: User): AuthToken
    fun createRefreshToken(user: User): AuthToken
}
