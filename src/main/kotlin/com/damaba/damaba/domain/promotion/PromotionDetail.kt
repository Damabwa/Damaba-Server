package com.damaba.damaba.domain.promotion

import com.damaba.damaba.domain.common.Address
import com.damaba.damaba.domain.common.PhotographyType
import com.damaba.damaba.domain.file.Image
import com.damaba.damaba.domain.promotion.constant.PromotionType
import com.damaba.damaba.domain.region.Region
import com.damaba.damaba.domain.user.User
import java.time.LocalDate

data class PromotionDetail(
    val id: Long,
    val author: User?,
    val promotionType: PromotionType,
    val title: String,
    val content: String,
    val address: Address,
    val externalLink: String?,
    val startedAt: LocalDate?,
    val endedAt: LocalDate?,
    val viewCount: Long,
    val saveCount: Long,
    val isSaved: Boolean,
    val photographyTypes: Set<PhotographyType>,
    val images: List<Image>,
    val activeRegions: Set<Region>,
    val hashtags: Set<String>,
)
