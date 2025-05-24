package com.damaba.damaba.infrastructure.kakao

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader

@FeignClient(
    name = "KakaoKApiClient",
    configuration = [KakaoKApiFeignConfig::class],
)
interface KakaoKApiFeignClient {
    /**
     * [사용자 정보 가져오기 API](https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#req-user-info) 사용.
     * Kakao access token으로 사용자 정보를 조회한다.
     *
     * @param authorizationHeader kakao access token (with Bearer type prefix)
     * @return 사용자 정보
     */
    @GetMapping(
        value = ["/v2/user/me"],
        headers = ["Content-type=application/x-www-form-urlencoded;charset=utf-8"],
    )
    fun getUserInfo(@RequestHeader("Authorization") authorizationHeader: String): KakaoUserInfoResponse
}
