package com.damaba.user.infrastructure.user

import com.damaba.user.domain.user.User
import com.damaba.user.domain.user.UserRepository
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryImpl(private val userJpaRepository: UserJpaRepository) : UserRepository {
    override fun findById(id: Long): User? {
        val optionalUser = userJpaRepository.findById(id).map(UserJpaEntity::toDomain)
        return if (optionalUser.isPresent) optionalUser.get() else null
    }

    override fun findByOAuthLoginUid(oAuthLoginUid: String): User? =
        userJpaRepository.findByOAuthLoginUid(oAuthLoginUid)?.toDomain()

    override fun save(user: User): User {
        val userJpaEntity = userJpaRepository.save(UserJpaEntity.from(user))
        return userJpaEntity.toDomain()
    }
}
