package com.damaba.user.infra.user

import com.damaba.user.domain.user.User
import com.damaba.user.domain.user.constant.LoginType
import com.damaba.user.infra.common.BaseJpaEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "user")
class UserJpaEntity(
    id: Long,
    oAuthLoginUid: String,
    loginType: LoginType,
    createdAt: LocalDateTime,
    updatedAt: LocalDateTime,
    deletedAt: LocalDateTime?,
) : BaseJpaEntity(createdAt, updatedAt) {
    companion object {
        fun from(user: User): UserJpaEntity = UserJpaEntity(
            id = user.id,
            oAuthLoginUid = user.oAuthLoginUid,
            loginType = user.loginType,
            createdAt = user.createdAt,
            updatedAt = user.updatedAt,
            deletedAt = user.deletedAt,
        )
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Long = id
        private set

    @Column(name = "o_auth_login_uid", nullable = false)
    var oAuthLoginUid: String = oAuthLoginUid
        private set

    @Enumerated(EnumType.STRING)
    @Column(name = "login_type", nullable = false)
    var loginType: LoginType = loginType
        private set

    @Column(name = "deleted_at")
    var deletedAt: LocalDateTime? = deletedAt
        private set

    fun toDomain(): User = User(
        id = id,
        oAuthLoginUid = oAuthLoginUid,
        loginType = loginType,
        createdAt = createdAt,
        updatedAt = updatedAt,
        deletedAt = deletedAt,
    )
}
