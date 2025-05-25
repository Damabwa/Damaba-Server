package com.damaba.damaba.application.photographer.dto

import com.damaba.damaba.domain.common.Address
import com.damaba.damaba.domain.file.Image
import com.damaba.damaba.domain.photographer.PhotographerValidator

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
