package com.damaba.user.infrastructure.user

import org.springframework.data.jpa.repository.JpaRepository

interface UserProfileImageJpaRepository : JpaRepository<UserProfileImageJpaEntity, Long> {
    fun findByUrl(url: String): UserProfileImageJpaEntity?
}
