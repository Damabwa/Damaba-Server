package com.damaba.damaba.domain.common

import com.damaba.common_exception.ValidationException

object AddressValidator {
    fun validate(address: Address) {
        if (address.sido.isBlank()) {
            throw ValidationException("시/도는 필수 입력 값입니다.")
        }
        if (address.sigungu.isBlank()) {
            throw ValidationException("시/군/구는 필수 입력 값입니다.")
        }
        if (address.roadAddress.isNullOrBlank() && address.jibunAddress.isNullOrBlank()) {
            throw ValidationException("도로명 주소나 지번 주소 중 하나는 반드시 입력해야 합니다.")
        }
    }
}
