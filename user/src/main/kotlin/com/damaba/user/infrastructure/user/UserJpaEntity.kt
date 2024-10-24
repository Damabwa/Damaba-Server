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
import java.time.LocalDate

@Table(name = "`user`")
@Entity
class UserJpaEntity(
    roles: Set<UserRoleType>,
    loginType: LoginType,
    oAuthLoginUid: String,
    nickname: String,
    profileImageUrl: String,
    gender: Gender,
    birthDate: LocalDate,
    instagramId: String?,
) : BaseJpaTimeEntity() {
    companion object {
        fun from(user: User): UserJpaEntity = UserJpaEntity(
            roles = user.roles,
            loginType = user.loginType,
            oAuthLoginUid = user.oAuthLoginUid,
            nickname = user.nickname,
            profileImageUrl = user.profileImageUrl,
            gender = user.gender,
            birthDate = user.birthDate,
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

    @Column(name = "nickname", length = 7, nullable = false, unique = true)
    var nickname: String = nickname
        private set

    @Column(name = "profile_image_url", nullable = false)
    var profileImageUrl: String = profileImageUrl
        private set

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    var gender: Gender = gender
        private set

    @Column(name = "birth_date", nullable = false)
    var birthDate: LocalDate = birthDate
        private set

    @Column(name = "instagram_id", length = 30, nullable = true)
    var instagramId: String? = instagramId
        private set

    fun toDomain(): User = User(
        id = this.id,
        roles = this.roles,
        oAuthLoginUid = this.oAuthLoginUid,
        loginType = this.loginType,
        nickname = this.nickname,
        profileImageUrl = this.profileImageUrl,
        gender = this.gender,
        birthDate = this.birthDate,
        instagramId = this.instagramId,
    )

    fun update(user: User) {
        this.nickname = user.nickname
        this.birthDate = user.birthDate
        this.gender = user.gender
        this.instagramId = user.instagramId
        this.profileImageUrl = user.profileImageUrl
    }
}
