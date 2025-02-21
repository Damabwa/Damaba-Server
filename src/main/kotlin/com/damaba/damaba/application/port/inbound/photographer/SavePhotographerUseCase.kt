package com.damaba.damaba.application.port.inbound.photographer

import com.damaba.damaba.domain.photographer.exception.AlreadySavedPhotographerException

interface SavePhotographerUseCase {
    /**
     * 사진작가를 저장한다.
     *
     * @param command
     * @throws AlreadySavedPhotographerException 이미 저장한 사진작가인 경우
     */
    fun savePhotographer(command: Command)

    data class Command(
        val reqUserId: Long,
        val photographerId: Long,
    )
}
