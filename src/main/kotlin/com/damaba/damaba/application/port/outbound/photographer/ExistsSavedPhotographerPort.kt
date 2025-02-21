package com.damaba.damaba.application.port.outbound.photographer

interface ExistsSavedPhotographerPort {
    fun existsByUserIdAndPhotographerId(userId: Long, photographerId: Long): Boolean
}
