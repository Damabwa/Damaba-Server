package com.damaba.user.domain.user

import com.damaba.user.domain.user.constant.LoginType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(private val userRepository: UserRepository) {
    @Transactional(readOnly = true)
    fun findUserByOAuthLoginUid(oAuthLoginUid: String): User? =
        userRepository.findByOAuthLoginUid(oAuthLoginUid)

    @Transactional
    fun createNewUser(oAuthLoginUid: String, loginType: LoginType): User =
        userRepository.save(User(oAuthLoginUid = oAuthLoginUid, loginType = loginType))
}
