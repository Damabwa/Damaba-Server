package com.damaba.damaba.application.port.outbound.photographer

interface ExistsPhotographerSavePort {
    fun existsByUserIdAndPhotographerId(userId: Long, photographerId: Long): Boolean
}
