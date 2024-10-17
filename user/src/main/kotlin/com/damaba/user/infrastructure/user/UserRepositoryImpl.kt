package com.damaba.user.infrastructure.user

import com.damaba.user.domain.user.User
import com.damaba.user.domain.user.UserRepository
import com.damaba.user.domain.user.exception.UserNotFoundException
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryImpl(
    private val userJpaRepository: UserJpaRepository,
    private val userProfileImageJpaRepository: UserProfileImageJpaRepository,
) : UserRepository {
    override fun findById(id: Long): User? =
        findUserJpaEntityById(id)?.toDomain()

    override fun findByOAuthLoginUid(oAuthLoginUid: String): User? =
        userJpaRepository.findByOAuthLoginUid(oAuthLoginUid)?.toDomain()

    override fun getById(id: Long): User =
        getUserJpaEntityById(id).toDomain()

    override fun existsByNickname(nickname: String): Boolean =
        userJpaRepository.existsByNickname(nickname)

    override fun save(user: User): User {
        val userJpaEntity = userJpaRepository.save(UserJpaEntity.from(user))
        return userJpaEntity.toDomain()
    }

    override fun update(user: User): User {
        val originalUser = getUserJpaEntityById(user.id)

        if (originalUser.profileImageUrl != user.profileImageUrl) {
            val originalProfileImage = userProfileImageJpaRepository.findByUrl(originalUser.profileImageUrl)
            originalProfileImage?.delete()
            userProfileImageJpaRepository.save(
                UserProfileImageJpaEntity(
                    userId = user.id,
                    url = user.profileImageUrl,
                    name = extractProfileImageFileName(user.profileImageUrl),
                ),
            )
        }

        originalUser.update(user)
        return originalUser.toDomain()
    }

    private fun findUserJpaEntityById(id: Long): UserJpaEntity? =
        userJpaRepository.findById(id).orElseGet { null }

    private fun getUserJpaEntityById(id: Long): UserJpaEntity =
        userJpaRepository.findById(id).orElseThrow { UserNotFoundException() }

    // https://image.damaba.me/profile-image-1.jpg => profile-image-1
    private fun extractProfileImageFileName(profileImageUrl: String) =
        profileImageUrl.substringBeforeLast(".")
            .substringAfterLast("/")
}
