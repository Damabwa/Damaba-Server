package com.damaba.user.infrastructure.user

import com.damaba.user.infrastructure.common.BaseJpaEntity
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
    name: String,
) : BaseJpaEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    val id: Long = 0

    @Column(name = "user_id", nullable = false)
    var userId: Long = userId
        private set

    @Column(name = "name", nullable = false)
    var name: String = name
        private set

    @Column(name = "url", nullable = false)
    var url: String = url
        private set

    @Column(name = "deleted_at", nullable = true)
    var deletedAt: LocalDateTime? = null
        private set

    fun delete() {
        this.deletedAt = LocalDateTime.now()
    }
}
