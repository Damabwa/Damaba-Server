package com.damaba.damaba.domain.term

import com.damaba.damaba.application.term.TermItem

object TermValidator {
    private val userRequired = setOf(
        TermType.AGE_CONFIRMATION,
        TermType.PRIVACY_TERMS,
        TermType.SERVICE_TERMS,
    )
    private val photographerRequired = userRequired + TermType.PHOTOGRAPHER_TERMS

    fun validateUserRequired(items: List<TermItem>) {
        val agreedSet = items.filter { it.agreed }.map { it.type }.toSet()
        if (!agreedSet.containsAll(userRequired)) {
            throw IllegalArgumentException("(유저) 필수 약관에 모두 동의해야 합니다.")
        }
    }

    fun validatePhotographerRequired(items: List<TermItem>) {
        val agreedSet = items.filter { it.agreed }.map { it.type }.toSet()
        if (!agreedSet.containsAll(photographerRequired)) {
            throw IllegalArgumentException("(작가) 필수 약관에 모두 동의해야 합니다.")
        }
    }
}
