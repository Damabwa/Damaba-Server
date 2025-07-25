package com.damaba.damaba.domain.term

enum class TermType(
    val required: Boolean,
) {
    AGE_CONFIRMATION(required = true),
    SERVICE_TERMS(required = true),
    PRIVACY_TERMS(required = true),
    PHOTOGRAPHER_TERMS(required = true),
}
