package com.damaba.damaba.application.port.outbound.auth

interface ParseUserIdFromAuthTokenPort {
    /**
     * Token에 담긴 user id를 조회한다.
     */
    fun parseUserId(authToken: String): Long
}
