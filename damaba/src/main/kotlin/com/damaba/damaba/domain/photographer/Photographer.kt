package com.damaba.damaba.domain.photographer

import com.damaba.damaba.domain.common.Address
import com.damaba.damaba.domain.common.PhotographyType

data class Photographer(
    val userId: Long,
    val mainPhotographyTypes: Set<PhotographyType>,
    val contactLink: String?,
    val description: String?,
    val address: Address?,
    val businessSchedule: BusinessSchedule?,
    val portfolio: List<PhotographerPortfolioImage>,
    val activeRegions: List<PhotographerActiveRegion>,
)
