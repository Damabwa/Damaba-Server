@file:Suppress("ktlint:standard:filename")

package com.damaba.damaba.controller.term

import com.damaba.damaba.domain.term.TermType
import io.swagger.v3.oas.annotations.media.Schema

data class AgreementRequestItem(
    @Schema(description = "약관 항목", example = "SERVICE_TERMS")
    val type: TermType,

    @Schema(description = "사용자의 동의 여부")
    val agreed: Boolean,
)
