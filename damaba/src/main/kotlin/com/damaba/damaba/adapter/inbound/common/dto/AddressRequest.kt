package com.damaba.damaba.adapter.inbound.common.dto

import com.damaba.damaba.domain.common.Address
import io.swagger.v3.oas.annotations.media.Schema

data class AddressRequest(
    @Schema(description = "시/도 이름", example = "경기")
    val sido: String,

    @Schema(description = "시/군/구 이름", example = "성남시 분당구")
    val sigungu: String,

    @Schema(description = "도로명 주소", example = "경기 성남시 분당구 판교역로 166")
    val roadAddress: String,

    @Schema(description = "지번 주소", example = "경기 성남시 분당구 백현동 532")
    val jibunAddress: String,
) {
    fun toDomain(): Address = Address(sido, sigungu, roadAddress, jibunAddress)
}
