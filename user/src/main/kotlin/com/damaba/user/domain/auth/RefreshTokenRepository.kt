package com.damaba.user.domain.auth

interface RefreshTokenRepository {
    /**
     * `userId`(key)로 refresh token을 조회한다.
     *
     * @param userId
     * @return 조회된 refresh token
     */
    fun findByUserId(userId: Long): RefreshToken?

    /**
     * Refresh token을 저장한다.
     *
     * @param refreshToken 저장할 refresh token
     * @param ttlMillis ttl(ms)
     */
    fun save(refreshToken: RefreshToken, ttlMillis: Long)
}
