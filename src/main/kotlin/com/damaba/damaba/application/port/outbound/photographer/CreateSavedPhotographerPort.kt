package com.damaba.damaba.application.port.outbound.photographer

import com.damaba.damaba.domain.photographer.SavedPhotographer

interface CreateSavedPhotographerPort {
    fun create(savedPhotographer: SavedPhotographer)
}
