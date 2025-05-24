package com.damaba.damaba.infrastructure.user

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface UserProfileImageJpaRepository : JpaRepository<UserProfileImageJpaEntity, Long> {
    @Query("SELECT upi FROM UserProfileImageJpaEntity upi WHERE upi.deletedAt IS NULL AND upi.url = :url")
    fun findByUrl(url: String): UserProfileImageJpaEntity?
}
