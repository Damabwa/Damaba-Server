package com.damaba.user.domain.user

import com.damaba.user.domain.user.constant.LoginType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(private val userRepository: UserRepository) {
    /**
     * `OAuthLoginUid`로 유저를 단건 조회한다.
     *
     * @param oAuthLoginUid 조회하고자 하는 유저의 OAuth login user id
     * @return 조회된 유저
     */
    @Transactional(readOnly = true)
    fun findUserByOAuthLoginUid(oAuthLoginUid: String): User? =
        userRepository.findByOAuthLoginUid(oAuthLoginUid)

    /**
     * 신규 유저를 생성 및 저장한다.
     *
     * @param oAuthLoginUid
     * @param loginType
     * @return 생성된 유저
     */
    @Transactional
    fun createNewUser(oAuthLoginUid: String, loginType: LoginType): User =
        userRepository.save(User(oAuthLoginUid = oAuthLoginUid, loginType = loginType))
}
