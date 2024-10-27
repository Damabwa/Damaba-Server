package com.damaba.user.adapter.outbound.kakao

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class KakaoUserInfoResponse(val id: String)
