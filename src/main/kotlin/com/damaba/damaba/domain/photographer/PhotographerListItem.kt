package com.damaba.damaba.domain.photographer

import com.damaba.damaba.domain.common.PhotographyType
import com.damaba.damaba.domain.file.Image

/**
 * '작가님을 만나봐(작가 리스트)' 페이지에서 각 리스트 아이템을 담을 도메인 객체
 */
data class PhotographerListItem(
    val id: Long,
    val nickname: String,
    val profileImage: Image?,
    val isSaved: Boolean,
    val mainPhotographyTypes: Set<PhotographyType>,
)
