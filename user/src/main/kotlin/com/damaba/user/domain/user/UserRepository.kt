package com.damaba.user.domain.user

interface UserRepository {
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
     */
    fun getById(id: Long): User

    /**
     * `nickname`이 사용중인 닉네임인지 확인한다.
     *
     * @param nickname 사용중인지 확인할 닉네임
     * @return `nickname`이 사용중이라면 `true`, 사용중이 아니라면 `false`
     */
    fun existsByNickname(nickname: String): Boolean

    /**
     * 신규 유저를 저장한다.
     *
     * @param user 저장할 신규 유저
     * @return 저장된 유저
     */
    fun save(user: User): User

    /**
     * 유저 정보를 수정한다.
     *
     * @param user 수정하고자 하는 유저 정보
     * @return 수정된 유저
     */
    fun update(user: User): User
}
