@file:Suppress("ktlint:standard:filename")

package com.damaba.damaba.controller.term

import com.damaba.damaba.domain.term.TermType
import io.swagger.v3.oas.annotations.media.Schema

data class TermMetadataResponse(
    @Schema(description = "약관 항목", example = "SERVICE_TERMS")
    val type: TermType,

    @Schema(description = "필수 약관 여부", example = "true")
    val required: Boolean,
)
