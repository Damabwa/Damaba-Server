package com.damaba.user.infrastructure.auth

import com.damaba.user.domain.auth.OAuthService
import com.damaba.user.domain.user.constant.LoginType
import com.damaba.user.infrastructure.kakao.KakaoKApiFeignClient
import org.springframework.stereotype.Service

@Service
class OAuthServiceImpl(private val kakaoKApiClient: KakaoKApiFeignClient) : OAuthService {
    companion object {
        private const val BEARER_PREFIX = "Bearer "
    }

    override fun getOAuthLoginUid(platform: LoginType, authKey: String): String {
        if (platform == LoginType.KAKAO) {
            return getKakaoUserId(authKey)
        }
        throw IllegalArgumentException("Unsupported oauth login platform: $platform")
    }

    private fun getKakaoUserId(kakaoAccessToken: String): String =
        kakaoKApiClient.getUserInfo(authorizationHeader = BEARER_PREFIX + kakaoAccessToken).id
}
