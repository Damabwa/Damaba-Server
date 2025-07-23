package com.damaba.damaba.domain.term

class Term(
    val id: Long?,
    val userId: Long,
    val type: TermType,
    agreed: Boolean,
) {
    var agreed: Boolean = agreed
        protected set
}
