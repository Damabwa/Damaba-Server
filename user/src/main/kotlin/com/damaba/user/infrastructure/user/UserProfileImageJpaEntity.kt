package com.damaba.user.infrastructure.user

import com.damaba.user.domain.user.UserProfileImage
import com.damaba.user.infrastructure.common.BaseJpaTimeEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Table(name = "user_profile_image")
@Entity
class UserProfileImageJpaEntity(
    userId: Long,
    url: String,
    storedName: String,
) : BaseJpaTimeEntity() {
    companion object {
        fun from(userProfileImage: UserProfileImage): UserProfileImageJpaEntity = UserProfileImageJpaEntity(
            userId = userProfileImage.userId,
            url = userProfileImage.url,
            storedName = userProfileImage.storedName,
        )
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    val id: Long = 0

    @Column(name = "user_id", nullable = false)
    val userId: Long = userId

    @Column(name = "url", nullable = false)
    val url: String = url

    @Column(name = "stored_name", nullable = false)
    val storedName: String = storedName

    @Column(name = "deleted_at", nullable = true)
    val deletedAt: LocalDateTime? = null

    fun toDomain(): UserProfileImage = UserProfileImage(
        id = this.id,
        userId = this.userId,
        url = this.url,
        storedName = this.storedName,
    )
}
