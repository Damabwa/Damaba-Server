package com.damaba.damaba.application.port.outbound.photographer

import com.damaba.damaba.domain.photographer.PhotographerSave

interface CreatePhotographerSavePort {
    fun create(photographerSave: PhotographerSave)
}
