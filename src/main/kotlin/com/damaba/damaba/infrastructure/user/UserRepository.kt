package com.damaba.damaba.infrastructure.user

import com.damaba.damaba.domain.user.User
import com.damaba.damaba.domain.user.exception.UserNotFoundException

interface UserRepository {
    /**
     * 신규 유저를 저장한다.
     *
     * @param user 저장할 신규 유저
     * @return 저장된 유저
     */
    fun create(user: User): User

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

    /**
     * @param id 조회하고자 하는 유저의 id
     * @return 조회된 유저
     * @throws UserNotFoundException `id`와 일치하는 유저를 찾을 수 없는 경우
     */
    fun getById(id: Long): User

    /**
     * `nickname`이 사용중인 닉네임인지 확인한다.
     *
     * @param nickname 사용중인지 확인할 닉네임
     * @return `nickname`이 사용중이라면 `true`, 사용중이 아니라면 `false`
     */
    fun existsNickname(nickname: String): Boolean

    /**
     * 유저 정보를 수정한다.
     *
     * @param user 수정하고자 하는 유저 정보
     * @return 수정된 유저
     */
    fun update(user: User): User

    /**
     * 프로필 이미지를 삭제한다.
     * `profileImageUrl`에 해당하는 이미지가 존재하지 않는 경우 무시된다.
     *
     * @param profileImageUrl 삭제할 프로필 이미지의 url
     */
    fun deleteProfileImageByUrl(profileImageUrl: String)
}
