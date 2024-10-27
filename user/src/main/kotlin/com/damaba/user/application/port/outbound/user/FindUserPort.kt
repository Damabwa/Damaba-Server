package com.damaba.user.application.port.outbound.user

import com.damaba.user.domain.user.User

interface FindUserPort {
    /**
     * @param id 조회하고자 하는 유저의 id
     * @return 조회된 유저
     */
    fun findById(id: Long): User?

    /**
     * @param oAuthLoginUid 조회하고자 하는 유저의 OAuth login user id
     * @return 조회된 유저
     */
    fun findByOAuthLoginUid(oAuthLoginUid: String): User?
}
