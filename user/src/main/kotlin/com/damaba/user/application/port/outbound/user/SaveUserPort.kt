package com.damaba.user.application.port.outbound.user

import com.damaba.user.domain.user.User

interface SaveUserPort {
    /**
     * 신규 유저를 저장한다.
     *
     * @param user 저장할 신규 유저
     * @return 저장된 유저
     */
    fun save(user: User): User
}
