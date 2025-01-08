package com.damaba.damaba.adapter.outbound.user

import com.damaba.damaba.application.port.outbound.user.CheckNicknameExistencePort
import com.damaba.damaba.application.port.outbound.user.FindUserPort
import com.damaba.damaba.application.port.outbound.user.GetUserPort
import com.damaba.damaba.application.port.outbound.user.SaveUserPort
import com.damaba.damaba.application.port.outbound.user.UpdateUserPort
import com.damaba.damaba.domain.user.User
import com.damaba.damaba.domain.user.exception.UserNotFoundException
import com.damaba.damaba.mapper.UserMapper
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
    override fun findById(id: Long): User? = findUserJpaEntityById(id)?.let { UserMapper.INSTANCE.toUser(it) }

    override fun findByOAuthLoginUid(oAuthLoginUid: String): User? = userJpaRepository.findByOAuthLoginUid(oAuthLoginUid)?.let { UserMapper.INSTANCE.toUser(it) }

    override fun getById(id: Long): User = UserMapper.INSTANCE.toUser(getUserJpaEntityById(id))

    override fun doesNicknameExist(nickname: String): Boolean = userJpaRepository.existsByNickname(nickname)

    override fun save(user: User): User {
        val userJpaEntity = userJpaRepository.save(UserMapper.INSTANCE.toUserJpaEntity(user))
        return UserMapper.INSTANCE.toUser(userJpaEntity)
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
        return UserMapper.INSTANCE.toUser(userJpaEntity)
    }

    private fun findUserJpaEntityById(id: Long): UserJpaEntity? = userJpaRepository.findById(id).orElseGet { null }

    private fun getUserJpaEntityById(id: Long): UserJpaEntity = userJpaRepository.findById(id).orElseThrow { UserNotFoundException() }
}
