package com.damaba.user.adapter.outbound.user

import com.damaba.user.adapter.outbound.common.BaseJpaTimeEntity
import com.damaba.user.domain.user.User
import com.damaba.user.domain.user.constant.Gender
import com.damaba.user.domain.user.constant.LoginType
import com.damaba.user.domain.user.constant.UserRoleType
import com.damaba.user.domain.user.constant.UserType
import com.damaba.user.mapper.UserMapper
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
    type: UserType,
    roles: Set<UserRoleType>,
    loginType: LoginType,
    oAuthLoginUid: String,
    nickname: String,
    profileImage: UserProfileImageJpaEmbeddable,
    gender: Gender,
    instagramId: String?,
) : BaseJpaTimeEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    val id: Long = 0

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    var type: UserType = type
        private set

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
    var profileImage: UserProfileImageJpaEmbeddable = profileImage
        private set

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    var gender: Gender = gender
        private set

    @Column(name = "instagram_id", length = 30, nullable = true)
    var instagramId: String? = instagramId
        private set

    fun update(user: User) {
        this.type = user.type
        this.roles = user.roles
        this.nickname = user.nickname
        this.gender = user.gender
        this.instagramId = user.instagramId
        this.profileImage = UserMapper.INSTANCE.toUserProfileImageJpaEmbeddable(user.profileImage)
    }
}
