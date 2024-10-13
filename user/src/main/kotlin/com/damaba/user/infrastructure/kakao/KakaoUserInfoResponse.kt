package com.damaba.user.infrastructure.kakao

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class KakaoUserInfoResponse(val id: String)
