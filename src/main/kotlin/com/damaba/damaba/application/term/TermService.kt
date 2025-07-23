package com.damaba.damaba.application.term

import com.damaba.damaba.domain.term.Term
import com.damaba.damaba.infrastructure.term.TermRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class TermService(
    private val termRepo: TermRepository,
) {
    // 일반 유저용 약관 동의 내역 저장
    @Transactional
    fun acceptUserTerms(command: AcceptUserTermsCommand) {
        val userTerms = command.terms.map { item ->
            Term(
                id = 0L,
                userId = command.userId,
                type = item.type,
                agreed = item.agreed,
            )
        }
        termRepo.saveAll(userTerms)
    }

    // 사진 작가용 약관 동의 내역 저장
    @Transactional
    fun acceptPhotographerTerms(command: AcceptPhotographerTermsCommand) {
        val photoTerm = command.terms.map { item ->
            Term(
                id = 0L,
                userId = command.userId,
                type = item.type,
                agreed = item.agreed,
            )
        }
        termRepo.saveAll(photoTerm)
    }
}
