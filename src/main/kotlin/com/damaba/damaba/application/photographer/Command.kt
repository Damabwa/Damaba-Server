package com.damaba.damaba.application.photographer

import com.damaba.damaba.application.term.TermItem
import com.damaba.damaba.domain.common.Address
import com.damaba.damaba.domain.common.constant.PhotographyType
import com.damaba.damaba.domain.file.Image
import com.damaba.damaba.domain.photographer.PhotographerValidator
import com.damaba.damaba.domain.region.Region
import com.damaba.damaba.domain.term.TermValidator
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
    val terms: List<TermItem>,
) {
    init {
        PhotographerValidator.validateNickname(nickname)
        if (instagramId != null) UserValidator.validateInstagramId(instagramId)
        PhotographerValidator.validateMainPhotographyTypes(mainPhotographyTypes)
        PhotographerValidator.validateActiveRegions(activeRegions)
        TermValidator.validatePhotographerRequired(terms)
    }
}

data class SavePhotographerCommand(
    val requestUserId: Long,
    val photographerId: Long,
)

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

data class UpdatePhotographerPageCommand(
    val photographerId: Long,
    val portfolio: List<Image>,
    val address: Address?,
    val instagramId: String?,
    val contactLink: String?,
    val description: String,
) {
    init {
        PhotographerValidator.validatePortfolio(portfolio)
        PhotographerValidator.validateDescription(description)
    }
}

data class UnsavePhotographerCommand(
    val requestUserId: Long,
    val photographerId: Long,
)
