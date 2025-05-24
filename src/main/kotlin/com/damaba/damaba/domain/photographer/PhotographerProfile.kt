package com.damaba.damaba.domain.photographer

import com.damaba.damaba.domain.common.constant.PhotographyType
import com.damaba.damaba.domain.file.Image
import com.damaba.damaba.domain.region.Region

data class PhotographerProfile(
    val nickname: String,
    val profileImage: Image?,
    val mainPhotographyTypes: Set<PhotographyType>,
    val activeRegions: Set<Region>,
)
