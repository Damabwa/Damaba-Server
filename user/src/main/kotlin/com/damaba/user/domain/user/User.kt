package com.damaba.user.domain.user

import com.damaba.user.domain.user.constant.LoginType
import com.damaba.user.domain.user.constant.UserRoleType
import java.time.LocalDateTime

data class User(
    val id: Long = 0,
    val roles: Set<UserRoleType> = setOf(UserRoleType.ROLE_USER),
    val oAuthLoginUid: String,
    val loginType: LoginType,
    val createdAt: LocalDateTime = LocalDateTime.MIN,
    val updatedAt: LocalDateTime = LocalDateTime.MIN,
) {
    // TODO: 현재는 회원 정보가 수정된 적이 있다면 초기 회원 정보 입력 과정을 거친 것으로 간주하나, 추후 닉네임 등을 통해 추가 검증 고려
    val isRegistrationCompleted
        get() = this.createdAt != this.updatedAt
}
