package com.damaba.damaba.controller.term

import com.damaba.damaba.application.term.AcceptPhotographerTermsCommand
import com.damaba.damaba.application.term.AcceptUserTermsCommand
import com.damaba.damaba.application.term.TermItem
import com.damaba.damaba.domain.term.TermType
import io.swagger.v3.oas.annotations.media.Schema

data class AgreementRequestItem(
    @Schema(description = "약관 항목", example = "SERVICE_TERMS")
    val type: TermType,

    @Schema(description = "사용자의 동의 여부")
    val agreed: Boolean,
)

data class UserTermRequest(
    @Schema(
        description = "사용자가 동의한 약관 항목들",
    )
    val agreements: List<AgreementRequestItem>,
) {
    fun toUserCommand(userId: Long): AcceptUserTermsCommand = AcceptUserTermsCommand(
        userId = userId,
        terms = agreements.map { TermItem(it.type, it.agreed) },
    )

    fun toPhotographerCommand(userId: Long): AcceptPhotographerTermsCommand = AcceptPhotographerTermsCommand(
        userId = userId,
        terms = agreements.map { TermItem(it.type, it.agreed) },
    )
}
