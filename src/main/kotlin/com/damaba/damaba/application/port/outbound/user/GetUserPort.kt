package com.damaba.user.application.port.outbound.user

import com.damaba.damaba.domain.user.User
import com.damaba.user.domain.user.exception.UserNotFoundException

interface GetUserPort {
    /**
     * @param id 조회하고자 하는 유저의 id
     * @return 조회된 유저
     * @throws UserNotFoundException `id`와 일치하는 유저를 찾을 수 없는 경우
     */
    fun getById(id: Long): User
}
