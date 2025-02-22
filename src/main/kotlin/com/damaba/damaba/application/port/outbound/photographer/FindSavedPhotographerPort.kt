package com.damaba.damaba.application.port.outbound.photographer

import com.damaba.damaba.domain.photographer.SavedPhotographer

interface FindSavedPhotographerPort {
    fun findByUserIdAndPhotographerId(userId: Long, photographerId: Long): SavedPhotographer?
}
