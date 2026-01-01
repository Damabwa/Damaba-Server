package com.damaba.damaba.domain.promotion

import com.damaba.damaba.domain.common.constant.PhotographyType
import com.damaba.damaba.domain.file.Image
import com.damaba.damaba.domain.region.Region
import com.damaba.damaba.domain.user.User
import java.time.LocalDate

/**
 * '이벤트로 담아봐(프로모션 리스트)' 페이지에서 각 리스트 아이템을 담을 도메인 객체
 */
data class PromotionListItem(
    val id: Long,
    val author: User?,
    val title: String,
    val startedAt: LocalDate?,
    val endedAt: LocalDate?,
    val saveCount: Long,
    val isSaved: Boolean,
    val isAuthorHidden: Boolean,
    val photographyTypes: Set<PhotographyType>,
    val images: List<Image>,
    val activeRegions: Set<Region>,
    val hashtags: Set<String>,
)
