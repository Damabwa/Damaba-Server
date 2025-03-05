package com.damaba.damaba.application.port.inbound.photographer

import com.damaba.damaba.domain.common.PhotographyType
import com.damaba.damaba.domain.file.Image
import com.damaba.damaba.domain.photographer.Photographer
import com.damaba.damaba.domain.photographer.PhotographerValidator
import com.damaba.damaba.domain.region.Region

interface UpdatePhotographerProfileUseCase {
    fun updatePhotographerProfile(command: Command): Photographer

    data class Command(
        val photographerId: Long,
        val nickname: String,
        val profileImage: Image,
        val mainPhotographyTypes: Set<PhotographyType>,
        val activeRegions: Set<Region>,
    ) {
        init {
            PhotographerValidator.validateNickname(nickname)
            PhotographerValidator.validateMainPhotographyTypes(mainPhotographyTypes)
            PhotographerValidator.validateActiveRegions(activeRegions)
        }
    }
}
