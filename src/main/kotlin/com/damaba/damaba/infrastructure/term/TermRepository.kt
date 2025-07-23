package com.damaba.damaba.infrastructure.term

import com.damaba.damaba.domain.term.Term

interface TermRepository {
    fun saveAll(terms: List<Term>): List<Term>
}
