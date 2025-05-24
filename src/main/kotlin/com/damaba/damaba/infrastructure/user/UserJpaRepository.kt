package com.damaba.damaba.infrastructure.user

import org.springframework.data.jpa.repository.JpaRepository

interface UserJpaRepository : JpaRepository<UserJpaEntity, Long> {
    fun findByOAuthLoginUid(oAuthLoginUid: String): UserJpaEntity?
    fun existsByNickname(nickname: String): Boolean
}
