package com.damaba.damaba.application.port.outbound.user

import com.damaba.damaba.domain.user.User

interface UpdateUserPort {
    /**
     * 유저 정보를 수정한다.
     *
     * @param user 수정하고자 하는 유저 정보
     * @return 수정된 유저
     */
    fun update(user: User): User
}
