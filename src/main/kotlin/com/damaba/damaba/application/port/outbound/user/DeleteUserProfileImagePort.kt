package com.damaba.damaba.application.port.outbound.user

interface DeleteUserProfileImagePort {
    /**
     * 프로필 이미지를 삭제한다.
     * `profileImageUrl`에 해당하는 이미지가 존재하지 않는 경우 무시된다.
     *
     * @param profileImageUrl 삭제할 프로필 이미지의 url
     */
    fun deleteByUrl(profileImageUrl: String)
}
