package com.damaba.damaba.application.photographer.dto

import com.damaba.damaba.domain.common.constant.PhotographyType
import com.damaba.damaba.domain.file.Image
import com.damaba.damaba.domain.photographer.PhotographerValidator
import com.damaba.damaba.domain.region.Region

data class UpdatePhotographerProfileCommand(
    val photographerId: Long,
    val nickname: String,
    val profileImage: Image?,
    val mainPhotographyTypes: Set<PhotographyType>,
    val activeRegions: Set<Region>,
) {
    init {
        PhotographerValidator.validateNickname(nickname)
        PhotographerValidator.validateMainPhotographyTypes(mainPhotographyTypes)
        PhotographerValidator.validateActiveRegions(activeRegions)
    }
}
