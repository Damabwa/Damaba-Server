package com.damaba.user.infrastructure.user

import com.damaba.user.domain.user.User
import com.damaba.user.domain.user.constant.LoginType
import com.damaba.user.domain.user.constant.UserRoleType
import com.damaba.user.infrastructure.common.BaseJpaTimeEntity
import com.damaba.user.infrastructure.user.converter.UserRoleTypesConverter
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Table(name = "user")
@Entity
class UserJpaEntity(
    id: Long,
    roles: Set<UserRoleType>,
    oAuthLoginUid: String,
    loginType: LoginType,
) : BaseJpaTimeEntity() {
    companion object {
        fun from(user: User): UserJpaEntity = UserJpaEntity(
            id = user.id,
            roles = user.roles,
            oAuthLoginUid = user.oAuthLoginUid,
            loginType = user.loginType,
        )
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Long = id
        private set

    @Convert(converter = UserRoleTypesConverter::class)
    @Column(name = "roles", nullable = false)
    var roles: Set<UserRoleType> = roles
        private set

    @Column(name = "o_auth_login_uid", nullable = false)
    var oAuthLoginUid: String = oAuthLoginUid
        private set

    @Enumerated(EnumType.STRING)
    @Column(name = "login_type", nullable = false)
    var loginType: LoginType = loginType
        private set

    fun toDomain(): User = User(
        id = this.id,
        roles = this.roles,
        oAuthLoginUid = this.oAuthLoginUid,
        loginType = this.loginType,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
    )
}
