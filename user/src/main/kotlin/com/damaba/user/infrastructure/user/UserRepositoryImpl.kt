package com.damaba.user.infrastructure.user

import com.damaba.user.domain.user.User
import com.damaba.user.domain.user.UserRepository
import com.damaba.user.domain.user.exception.UserNotFoundException
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryImpl(private val userJpaRepository: UserJpaRepository) : UserRepository {
    override fun findById(id: Long): User? =
        findJpaEntityById(id)?.toDomain()

    override fun findByOAuthLoginUid(oAuthLoginUid: String): User? =
        userJpaRepository.findByOAuthLoginUid(oAuthLoginUid)?.toDomain()

    override fun getById(id: Long): User =
        getJpaEntityById(id).toDomain()

    override fun existsByNickname(nickname: String): Boolean =
        userJpaRepository.existsByNickname(nickname)

    override fun save(user: User): User {
        val userJpaEntity = userJpaRepository.save(UserJpaEntity.from(user))
        return userJpaEntity.toDomain()
    }

    override fun update(user: User): User {
        val userJpaEntity = getJpaEntityById(user.id)
        userJpaEntity.update(user)
        return userJpaEntity.toDomain()
    }

    private fun findJpaEntityById(id: Long): UserJpaEntity? =
        userJpaRepository.findById(id).orElseGet { null }

    private fun getJpaEntityById(id: Long): UserJpaEntity =
        userJpaRepository.findById(id).orElseThrow { UserNotFoundException() }
}
