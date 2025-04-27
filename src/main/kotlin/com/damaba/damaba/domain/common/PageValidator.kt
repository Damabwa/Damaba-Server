package com.damaba.damaba.domain.common

import com.damaba.damaba.domain.exception.ValidationException

object PageValidator {
    fun validate(page: Int, pageSize: Int) {
        if (page < 0) {
            throw ValidationException("페이지 번호(page)는 0 이상의 정수여야 합니다.")
        }
        if (pageSize < 0) {
            throw ValidationException("페이지 크기(pageSize)는 0 이상의 정수여야 합니다.")
        }
    }
}
