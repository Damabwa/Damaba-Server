package com.damaba.damaba.adapter.outbound.user

import com.damaba.damaba.application.port.outbound.user.CreateUserPort
import com.damaba.damaba.application.port.outbound.user.DeleteUserProfileImagePort
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
    UpdateUserPort,
    DeleteUserProfileImagePort {
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

        if (userJpaEntity.profileImage?.url != user.profileImage?.url) {
            val originalProfileImageUrl = userJpaEntity.profileImage?.url
            if (originalProfileImageUrl != null) {
                this.deleteByUrl(originalProfileImageUrl)
            }

            val userProfileImage = user.profileImage
            if (userProfileImage != null) {
                userProfileImageJpaRepository.save(
                    UserProfileImageJpaEntity(
                        userId = user.id,
                        name = userProfileImage.name,
                        url = userProfileImage.url,
                    ),
                )
            }
        }
        userJpaEntity.update(user)
        return userJpaEntity.toUser()
    }

    override fun deleteByUrl(profileImageUrl: String) {
        userProfileImageJpaRepository.findByUrl(profileImageUrl)?.delete()
    }

    private fun findUserJpaEntityById(id: Long): UserJpaEntity? = userJpaRepository.findById(id).orElse(null)

    private fun getUserJpaEntityById(id: Long): UserJpaEntity = userJpaRepository.findById(id).orElseThrow { UserNotFoundException() }
}
