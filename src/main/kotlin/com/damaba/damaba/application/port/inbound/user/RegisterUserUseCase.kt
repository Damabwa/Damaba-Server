package com.damaba.damaba.application.port.inbound.user

import com.damaba.damaba.domain.user.User
import com.damaba.damaba.domain.user.UserValidator
import com.damaba.damaba.domain.user.constant.Gender
import com.damaba.damaba.domain.user.exception.NicknameAlreadyExistsException
import com.damaba.damaba.domain.user.exception.UserAlreadyRegisteredException
import com.damaba.damaba.domain.user.exception.UserNotFoundException

interface RegisterUserUseCase {
    /**
     * 유저를 등록한다. 즉, 유저 등록 정보를 수정한다.
     * '유저 등록 정보'란 회원가입 시 유저에게 입력받는 정보를 의미한다.
     *
     * @param command
     * @return 등록된 유저
     * @throws UserNotFoundException `userId`에 해당하는 유저를 찾을 수 없는 경우
     * @throws UserAlreadyRegisteredException 이미 등록된 유저인 경우
     * @throws NicknameAlreadyExistsException `nickname`을 다른 유저가 이미 사용중인 경우
     */
    fun register(command: Command): User

    data class Command(
        val userId: Long,
        val nickname: String,
        val gender: Gender,
        val instagramId: String?,
    ) {
        init {
            UserValidator.validateNickname(nickname)
            if (instagramId != null) UserValidator.validateInstagramId(instagramId)
        }
    }
}
