package com.damaba.damaba.util.fixture

import com.damaba.damaba.domain.user.User
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority

object SecurityFixture {
    fun createAuthenticationToken(user: User): Authentication =
        UsernamePasswordAuthenticationToken(
            user,
            null,
            user.roles
                .map { roleType -> "ROLE_$roleType" }
                .map { roleName -> SimpleGrantedAuthority(roleName) }
                .toMutableList(),
        )
}
