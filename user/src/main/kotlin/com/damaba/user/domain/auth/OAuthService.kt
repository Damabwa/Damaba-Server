package com.damaba.user.domain.auth

import com.damaba.user.domain.user.constant.LoginType

interface OAuthService {
    /**
     * OAuth login user id를 조회한다.
     *
     * 각 로그인 플랫폼별 `authKey`의 의미는 다음과 같다.
     * - Kakao: access token
     *
     * 각 로그인 플랫폼별 OAuth login user id의 의미는 다음과 같다.
     * - Kakao: user id(사용자 정보 조회 시 응답받는 id)
     *
     * @param platform OAuth 로그인 플랫폼
     * @param authKey
     * @return 조회된 OAuth login
     */
    fun getOAuthLoginUid(platform: LoginType, authKey: String): String
}
