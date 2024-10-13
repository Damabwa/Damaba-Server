package com.damaba.user.infrastructure.user

import org.springframework.data.jpa.repository.JpaRepository

interface UserJpaRepository : JpaRepository<UserJpaEntity, Long> {
    fun findByOAuthLoginUid(oAuthLoginUid: String): UserJpaEntity?
}
