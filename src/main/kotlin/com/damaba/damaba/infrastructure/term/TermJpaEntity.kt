package com.damaba.damaba.infrastructure.term

import com.damaba.damaba.domain.term.Term
import com.damaba.damaba.domain.term.TermType
import com.damaba.damaba.infrastructure.common.TimeTrackedJpaEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "term")
class TermJpaEntity(
    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    val type: TermType,

    agreed: Boolean,
) : TimeTrackedJpaEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    val id: Long = 0

    @Column(name = "agreed", nullable = false)
    var agreed: Boolean = agreed
        private set

    fun toTerm(): Term = Term(
        id = this.id,
        userId = this.userId,
        type = this.type,
        agreed = this.agreed,
    )

    companion object {
        fun fromTerm(term: Term): TermJpaEntity = TermJpaEntity(
            userId = term.userId,
            type = term.type,
            agreed = term.agreed,
        )
    }
}
