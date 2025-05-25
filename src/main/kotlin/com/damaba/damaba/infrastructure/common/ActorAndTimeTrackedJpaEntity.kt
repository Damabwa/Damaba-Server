package com.damaba.damaba.infrastructure.common

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.jpa.domain.support.AuditingEntityListener

@EntityListeners(AuditingEntityListener::class)
@MappedSuperclass
abstract class ActorAndTimeTrackedJpaEntity protected constructor(
    createdBy: Long = 0,
    updatedBy: Long = 0,
) : TimeTrackedJpaEntity() {
    @Column(name = "created_by", nullable = false, updatable = false)
    @CreatedBy
    var createdBy: Long = createdBy
        private set

    @Column(name = "updated_by", nullable = false)
    @LastModifiedBy
    var updatedBy: Long = updatedBy
        private set
}
