package com.damaba.damaba.infrastructure.term

import com.damaba.damaba.domain.term.Term
import org.springframework.stereotype.Repository

@Repository
class TermCoreRepository(
    private val jpaRepository: TermJpaRepository,
) : TermRepository {

    override fun saveAll(terms: List<Term>): List<Term> {
        val entities = terms.map { TermJpaEntity.fromTerm(it) }
        return jpaRepository.saveAll(entities).map { it.toTerm() }
    }
}
