package com.damaba.damaba.application.port.inbound.photographer

interface UnsavePhotographerUseCase {
    fun unsavePhotographer(command: Command)

    data class Command(
        val reqUserId: Long,
        val photographerId: Long,
    )
}
