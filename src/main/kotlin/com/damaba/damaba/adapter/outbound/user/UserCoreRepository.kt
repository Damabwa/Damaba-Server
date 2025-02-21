package com.damaba.damaba.adapter.outbound.user

import com.damaba.damaba.application.port.outbound.user.CreateUserPort
import com.damaba.damaba.application.port.outbound.user.ExistsNicknamePort
import com.damaba.damaba.application.port.outbound.user.FindUserPort
import com.damaba.damaba.application.port.outbound.user.GetUserPort
import com.damaba.damaba.application.port.outbound.user.UpdateUserPort
import com.damaba.damaba.domain.user.User
import com.damaba.damaba.domain.user.exception.UserNotFoundException
import org.springframework.stereotype.Repository

@Repository
class UserCoreRepository(
    private val userJpaRepository: UserJpaRepository,
    private val userProfileImageJpaRepository: UserProfileImageJpaRepository,
) : FindUserPort,
    GetUserPort,
    ExistsNicknamePort,
    CreateUserPort,
    UpdateUserPort {
    override fun findById(id: Long): User? = findUserJpaEntityById(id)?.toUser()

    override fun findByOAuthLoginUid(oAuthLoginUid: String): User? = userJpaRepository.findByOAuthLoginUid(oAuthLoginUid)?.toUser()

    override fun getById(id: Long): User = getUserJpaEntityById(id).toUser()

    override fun existsNickname(nickname: String): Boolean = userJpaRepository.existsByNickname(nickname)

    override fun create(user: User): User {
        val userJpaEntity = userJpaRepository.save(UserJpaEntity.from(user))
        return userJpaEntity.toUser()
    }

    override fun update(user: User): User {
        val userJpaEntity = getUserJpaEntityById(user.id)

        if (userJpaEntity.profileImage.url != user.profileImage.url) {
            val originalProfileImage = userProfileImageJpaRepository.findByUrl(userJpaEntity.profileImage.url)
            originalProfileImage?.delete()
            userProfileImageJpaRepository.save(
                UserProfileImageJpaEntity(
                    userId = user.id,
                    name = user.profileImage.name,
                    url = user.profileImage.url,
                ),
            )
        }

        userJpaEntity.update(user)
        return userJpaEntity.toUser()
    }

    private fun findUserJpaEntityById(id: Long): UserJpaEntity? = userJpaRepository.findById(id).orElseGet { null }

    private fun getUserJpaEntityById(id: Long): UserJpaEntity = userJpaRepository.findById(id).orElseThrow { UserNotFoundException() }
}
