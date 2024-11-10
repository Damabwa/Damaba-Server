package com.damaba.user.util

import com.damaba.user.domain.auth.AuthToken
import com.damaba.user.domain.user.User
import com.damaba.user.domain.user.UserProfileImage
import com.damaba.user.domain.user.constant.Gender
import com.damaba.user.domain.user.constant.LoginType
import com.damaba.user.domain.user.constant.UserRoleType
import com.damaba.user.util.RandomTestUtils.Companion.randomLong
import com.damaba.user.util.RandomTestUtils.Companion.randomString
import com.damaba.user.util.RandomTestUtils.Companion.randomUrl
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.time.LocalDateTime

object TestFixture {
    fun createAuthenticationToken(user: User): Authentication =
        UsernamePasswordAuthenticationToken(
            user,
            null,
            user.roles
                .map { roleType -> "ROLE_$roleType" }
                .map { roleName -> SimpleGrantedAuthority(roleName) }
                .toMutableList(),
        )

    fun createUser(
        id: Long = randomLong(),
        roles: Set<UserRoleType> = setOf(UserRoleType.USER),
        oAuthLoginUid: String = randomString(),
        loginType: LoginType = LoginType.KAKAO,
        nickname: String = randomString(len = 7),
        profileImage: UserProfileImage = UserProfileImage(randomString(), randomUrl()),
        gender: Gender = Gender.MALE,
        instagramId: String? = randomString(len = 30),
    ): User = User(
        id = id,
        roles = roles,
        oAuthLoginUid = oAuthLoginUid,
        loginType = loginType,
        nickname = nickname,
        profileImage = profileImage,
        gender = gender,
        instagramId = instagramId,
    )

    fun createAuthToken(
        value: String = randomString(),
        expiresAt: LocalDateTime = LocalDateTime.now(),
    ): AuthToken = AuthToken(
        value = value,
        expiresAt = expiresAt,
    )
}
