package com.damaba.damaba.adapter.inbound.promotion

import com.damaba.damaba.adapter.inbound.common.dto.ImageResponse
import com.damaba.damaba.adapter.inbound.region.dto.RegionResponse
import com.damaba.damaba.domain.common.PhotographyType
import com.damaba.damaba.domain.user.User
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

data class PromotionListItemResponse(
    @Schema(description = "Id of the promotion")
    val id: Long,

    @Schema(description = "프로모션 작성자 정보")
    val author: User?,

    @Schema(description = "제목", example = "이벤트 이름")
    val title: String,

    @Schema(description = "이벤트 시작일")
    val startedAt: LocalDate?,

    @Schema(description = "이벤트 종료일")
    val endedAt: LocalDate?,

    @Schema(description = "저장된 수", example = "5")
    val saveCount: Long,

    @Schema(description = "게시글 저장 여부. 이미 저장한 게시글이라면 <code>true</code>")
    val isSaved: Boolean,

    @Schema(description = "촬영 종류")
    val photographyTypes: Set<PhotographyType>,

    @Schema(description = "이미지 url 리스트")
    val images: List<ImageResponse>,

    @Schema(description = "활동 지역 리스트")
    val activeRegions: Set<RegionResponse>,

    @Schema(description = "해시태그 리스트", example = "[\"수원핫플\", \"스냅사진\"]")
    val hashtags: Set<String>,
)
