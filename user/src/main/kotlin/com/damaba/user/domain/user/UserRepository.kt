package com.damaba.user.domain.user

interface UserRepository {
    fun findById(id: Long): User?
    fun findByOAuthLoginUid(oAuthLoginUid: String): User?
    fun save(user: User): User
}
