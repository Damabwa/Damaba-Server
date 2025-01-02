package com.damaba.damaba.domain.common

import com.damaba.damaba.domain.exception.ValidationException

object AddressValidator {
    fun validate(address: Address) {
        if (address.sido.isBlank()) {
            throw ValidationException("시/도는 필수 입력 값입니다. 비어있을 수 없습니다.")
        }
        if (address.sigungu.isBlank()) {
            throw ValidationException("시/군/구는 필수 입력 값입니다. 비어있을 수 없습니다.")
        }
        if (address.roadAddress.isBlank()) {
            throw ValidationException("도로명 주소는 필수 입력 값입니다. 비어있을 수 없습니다.")
        }
        if (address.jibunAddress.isBlank()) {
            throw ValidationException("지번 주소는 필수 입력 값입니다. 비어있을 수 없습니다.")
        }
    }
}
