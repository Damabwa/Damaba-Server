package com.damaba.damaba.domain.photographer

import com.damaba.damaba.domain.common.Address
import com.damaba.damaba.domain.file.Image

data class PhotographerPage(
    val portfolio: List<Image>,
    val address: Address?,
    val instagramId: String?,
    val contactLink: String?,
    val description: String,
)
