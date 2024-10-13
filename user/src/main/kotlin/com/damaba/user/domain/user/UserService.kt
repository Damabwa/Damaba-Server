package com.damaba.user.domain.user

import com.damaba.user.domain.user.constant.Gender
import com.damaba.user.domain.user.constant.LoginType
import com.damaba.user.domain.user.exception.NicknameAlreadyExistsException
import com.damaba.user.domain.user.exception.UserNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(private val userRepository: UserRepository) {
    /**
     * @param userId 조회하고자 하는 유저의 id
     * @return 조회된 유저
     */
    @Transactional(readOnly = true)
    fun findUserById(userId: Long): User? =
        userRepository.findById(userId)

    /**
     * @param oAuthLoginUid 조회하고자 하는 유저의 OAuth login user id
     * @return 조회된 유저
     */
    @Transactional(readOnly = true)
    fun findUserByOAuthLoginUid(oAuthLoginUid: String): User? =
        userRepository.findByOAuthLoginUid(oAuthLoginUid)

    /**
     * @param userId 조회하고자 하는 유저의 id
     * @return 조회된 유저
     * @throws UserNotFoundException 조회된 유저가 없을 경우
     */
    @Transactional(readOnly = true)
    fun getUserById(userId: Long): User =
        userRepository.getById(userId)

    /**
     * 닉네임이 사용중인지 확인한다.
     *
     * @param nickname 사용중인지 확인할 닉네임
     * @return 사용중인 닉네임이라면 `true`, 사용중이지 않은 닉네임이라면 `false`
     */
    @Transactional(readOnly = true)
    fun doesNicknameExist(nickname: String): Boolean =
        userRepository.existsByNickname(nickname)

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

    /**
     * 유저 정보를 수정한다.
     *
     * @param userId 정보를 수정할 유저의 id
     * @param nickname 수정할 nickname
     * @param gender 수정할 gender
     * @param age 수정할 age
     * @param instagramId 수정할 instagramId
     * @return 수정된 유저 정보
     * @throws UserNotFoundException `userId`와 일치하는 유저 정보를 찾지 못한 경우
     * @throws NicknameAlreadyExistsException `nickname`이 이미 사용중인 닉네임인 경우
     */
    @Transactional
    fun updateUserInfo(
        userId: Long,
        nickname: String?,
        gender: Gender?,
        age: Int?,
        instagramId: String?,
    ): User {
        if (nickname != null && userRepository.existsByNickname(nickname)) {
            throw NicknameAlreadyExistsException(nickname)
        }
        val user = userRepository.getById(userId)
        return userRepository.update(user.update(nickname, gender, age, instagramId))
    }
}
