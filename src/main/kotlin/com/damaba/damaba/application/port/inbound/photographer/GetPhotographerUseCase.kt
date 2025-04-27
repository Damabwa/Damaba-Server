package com.damaba.damaba.application.port.inbound.photographer

import com.damaba.damaba.domain.photographer.Photographer

interface GetPhotographerUseCase {
    fun getPhotographer(id: Long): Photographer
}
