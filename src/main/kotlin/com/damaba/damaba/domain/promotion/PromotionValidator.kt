package com.damaba.damaba.domain.promotion

import com.damaba.damaba.domain.exception.ValidationException

object PromotionValidator {
    fun validateTitle(title: String) {
        if (title.isBlank() || title.length < 3 || title.length > 30) {
            throw ValidationException("프로모션 제목은 3~30 글자여야 합니다.")
        }
    }

    fun validateContent(content: String) {
        if (content.length >= 500) {
            throw ValidationException("프로모션 내용은 500 글자를 초과할 수 없습니다.")
        }
    }
}
