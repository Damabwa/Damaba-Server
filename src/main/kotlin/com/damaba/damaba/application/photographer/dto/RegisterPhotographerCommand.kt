package com.damaba.damaba.application.photographer.dto

import com.damaba.damaba.domain.common.constant.PhotographyType
import com.damaba.damaba.domain.file.Image
import com.damaba.damaba.domain.photographer.PhotographerValidator
import com.damaba.damaba.domain.region.Region
import com.damaba.damaba.domain.user.UserValidator
import com.damaba.damaba.domain.user.constant.Gender

data class RegisterPhotographerCommand(
    val userId: Long,
    val nickname: String,
    val gender: Gender,
    val instagramId: String?,
    val profileImage: Image,
    val mainPhotographyTypes: Set<PhotographyType>,
    val activeRegions: Set<Region>,
) {
    init {
        PhotographerValidator.validateNickname(nickname)
        if (instagramId != null) UserValidator.validateInstagramId(instagramId)
        PhotographerValidator.validateMainPhotographyTypes(mainPhotographyTypes)
        PhotographerValidator.validateActiveRegions(activeRegions)
    }
}
