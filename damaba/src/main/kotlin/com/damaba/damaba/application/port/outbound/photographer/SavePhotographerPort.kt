package com.damaba.damaba.application.port.outbound.photographer

import com.damaba.damaba.domain.photographer.Photographer
import com.damaba.user.domain.user.exception.UserNotFoundException

interface SavePhotographerPort {
    /**
     * `Photographer`를 저장한다.
     * `Photographer`는 `User`가 기존에 존재할 때만 생성/저장이 가능하다.
     *
     * @param photographer 저장할 `Photographer`
     * @return 저장된 `Photographer`
     * @throws UserNotFoundException 기존 유저 데이터가 존재하지 않는 경우.
     */
    fun saveIfUserExists(photographer: Photographer): Photographer
}
