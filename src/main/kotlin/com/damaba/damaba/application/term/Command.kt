package com.damaba.damaba.application.term

import com.damaba.damaba.domain.term.TermType
import com.damaba.damaba.domain.term.TermValidator

data class TermItem(
    val type: TermType,
    val agreed: Boolean,
)

data class AcceptUserTermsCommand(
    val userId: Long,
    val terms: List<TermItem>,
) {
    init {
        // TermValidator 에서 필수 약관 체크
        TermValidator.validateUserRequired(terms)
    }
}

data class AcceptPhotographerTermsCommand(
    val userId: Long,
    val terms: List<TermItem>,
) {
    init {
        // TermValidator 에서 유저+작가 필수 약관 체크
        TermValidator.validatePhotographerRequired(terms)
    }
}
