package com.damaba.damaba.infrastructure.auth

import com.damaba.damaba.application.port.outbound.auth.GetOAuthLoginUidPort
import com.damaba.damaba.domain.user.constant.LoginType
import com.damaba.damaba.infrastructure.kakao.KakaoKApiFeignClient
import org.springframework.stereotype.Component

@Component
class OAuthLoginProvider(private val kakaoKApiClient: KakaoKApiFeignClient) : GetOAuthLoginUidPort {
    override fun getOAuthLoginUid(platform: LoginType, authKey: String): String {
        if (platform == LoginType.KAKAO) {
            return getKakaoUserId(authKey)
        }
        throw IllegalArgumentException("Unsupported oauth login platform: $platform")
    }

    private fun getKakaoUserId(kakaoAccessToken: String): String = kakaoKApiClient.getUserInfo(authorizationHeader = BEARER_PREFIX + kakaoAccessToken).id

    companion object {
        private const val BEARER_PREFIX = "Bearer "
    }
}
