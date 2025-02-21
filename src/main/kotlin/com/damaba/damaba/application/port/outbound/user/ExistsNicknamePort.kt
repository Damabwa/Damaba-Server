package com.damaba.damaba.application.port.outbound.user

interface ExistsNicknamePort {
    /**
     * `nickname`이 사용중인 닉네임인지 확인한다.
     *
     * @param nickname 사용중인지 확인할 닉네임
     * @return `nickname`이 사용중이라면 `true`, 사용중이 아니라면 `false`
     */
    fun existsNickname(nickname: String): Boolean
}
