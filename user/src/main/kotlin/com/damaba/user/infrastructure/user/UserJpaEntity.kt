package com.damaba.user.infrastructure.user

import com.damaba.user.domain.user.User
import com.damaba.user.domain.user.constant.Gender
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

@Table(name = "`user`")
@Entity
class UserJpaEntity(
    roles: Set<UserRoleType>,
    loginType: LoginType,
    oAuthLoginUid: String,
    nickname: String,
    gender: Gender,
    age: Int,
    instagramId: String?,
) : BaseJpaTimeEntity() {
    companion object {
        fun from(user: User): UserJpaEntity = UserJpaEntity(
            roles = user.roles,
            loginType = user.loginType,
            oAuthLoginUid = user.oAuthLoginUid,
            nickname = user.nickname,
            gender = user.gender,
            age = user.age,
            instagramId = user.instagramId,
        )
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    val id: Long = 0

    @Convert(converter = UserRoleTypesConverter::class)
    @Column(name = "roles", nullable = false)
    var roles: Set<UserRoleType> = roles
        private set

    @Enumerated(EnumType.STRING)
    @Column(name = "login_type", nullable = false)
    var loginType: LoginType = loginType
        private set

    @Column(name = "o_auth_login_uid", nullable = false, unique = true)
    var oAuthLoginUid: String = oAuthLoginUid
        private set

    @Column(name = "nickname", nullable = false, unique = true)
    var nickname: String = nickname
        private set

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    var gender: Gender = gender
        private set

    @Column(name = "age", nullable = false)
    var age: Int = age
        private set

    @Column(name = "instagram_id", nullable = true)
    var instagramId: String? = instagramId
        private set

    fun toDomain(): User = User(
        id = this.id,
        roles = this.roles,
        oAuthLoginUid = this.oAuthLoginUid,
        loginType = this.loginType,
        nickname = this.nickname,
        gender = this.gender,
        age = this.age,
        instagramId = this.instagramId,
    )

    fun update(user: User) {
        this.nickname = user.nickname
        this.age = user.age
        this.gender = user.gender
        this.instagramId = user.instagramId
    }
}
