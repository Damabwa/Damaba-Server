package com.damaba.damaba.adapter.outbound.auth

import com.damaba.damaba.adapter.outbound.kakao.KakaoKApiFeignClient
import com.damaba.damaba.application.port.outbound.auth.GetOAuthLoginUidPort
import com.damaba.damaba.domain.user.constant.LoginType
import org.springframework.stereotype.Component

@Component
class OAuthLoginAdapter(private val kakaoKApiClient: KakaoKApiFeignClient) : GetOAuthLoginUidPort {
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
