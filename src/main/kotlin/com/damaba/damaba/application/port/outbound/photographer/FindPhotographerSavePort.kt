package com.damaba.damaba.application.port.outbound.photographer

import com.damaba.damaba.domain.photographer.PhotographerSave

interface FindPhotographerSavePort {
    fun findByUserIdAndPhotographerId(userId: Long, photographerId: Long): PhotographerSave?
}
