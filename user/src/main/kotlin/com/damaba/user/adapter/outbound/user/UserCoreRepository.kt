package com.damaba.user.adapter.outbound.user

import com.damaba.user.application.port.outbound.user.CheckNicknameExistencePort
import com.damaba.user.application.port.outbound.user.FindUserPort
import com.damaba.user.application.port.outbound.user.GetUserPort
import com.damaba.user.application.port.outbound.user.SaveUserPort
import com.damaba.user.application.port.outbound.user.UpdateUserPort
import com.damaba.user.domain.user.User
import com.damaba.user.domain.user.exception.UserNotFoundException
import org.springframework.stereotype.Repository

@Repository
class UserCoreRepository(
    private val userJpaRepository: UserJpaRepository,
    private val userProfileImageJpaRepository: UserProfileImageJpaRepository,
) : FindUserPort,
    GetUserPort,
    CheckNicknameExistencePort,
    SaveUserPort,
    UpdateUserPort {
    override fun findById(id: Long): User? =
        findUserJpaEntityById(id)?.toDomain()

    override fun findByOAuthLoginUid(oAuthLoginUid: String): User? =
        userJpaRepository.findByOAuthLoginUid(oAuthLoginUid)?.toDomain()

    override fun getById(id: Long): User =
        getUserJpaEntityById(id).toDomain()

    override fun doesNicknameExist(nickname: String): Boolean =
        userJpaRepository.existsByNickname(nickname)

    override fun save(user: User): User {
        val userJpaEntity = userJpaRepository.save(UserJpaEntity.from(user))
        return userJpaEntity.toDomain()
    }

    override fun update(user: User): User {
        val originalUserJpaEntity = getUserJpaEntityById(user.id)

        if (originalUserJpaEntity.profileImage.url != user.profileImage.url) {
            val originalProfileImage = userProfileImageJpaRepository.findByUrl(originalUserJpaEntity.profileImage.url)
            originalProfileImage?.delete()
            userProfileImageJpaRepository.save(
                UserProfileImageJpaEntity(
                    userId = user.id,
                    name = user.profileImage.name,
                    url = user.profileImage.url,
                ),
            )
        }

        originalUserJpaEntity.update(user)
        return originalUserJpaEntity.toDomain()
    }

    private fun findUserJpaEntityById(id: Long): UserJpaEntity? =
        userJpaRepository.findById(id).orElseGet { null }

    private fun getUserJpaEntityById(id: Long): UserJpaEntity =
        userJpaRepository.findById(id).orElseThrow { UserNotFoundException() }
}
