package com.damaba.damaba.adapter.inbound.promotion.dto

import com.damaba.damaba.adapter.inbound.common.dto.ImageResponse
import com.damaba.damaba.adapter.inbound.region.dto.RegionResponse
import com.damaba.damaba.adapter.inbound.user.dto.UserResponse
import com.damaba.damaba.domain.common.PhotographyType
import com.damaba.damaba.domain.promotion.constant.PromotionType
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

data class PromotionDetailResponse(
    @Schema(description = "Id of the promotion")
    val id: Long,

    @Schema(description = "프로모션 작성자의 id")
    val author: UserResponse?,

    @Schema(description = "프로모션 종류")
    val promotionType: PromotionType,

    @Schema(description = "제목", example = "이벤트 이름")
    val title: String,

    @Schema(description = "내용", example = "이 이벤트는 오늘부터 시작해서...")
    val content: String,

    @Schema(description = "이벤트 관련 외부 링크", example = "https://promotion-instagram-post")
    val externalLink: String?,

    @Schema(description = "이벤트 시작일")
    val startedAt: LocalDate?,

    @Schema(description = "이벤트 종료일")
    val endedAt: LocalDate?,

    @Schema(description = "조회수", example = "15")
    val viewCount: Long,

    @Schema(description = "저장된 수", example = "5")
    val saveCount: Int,

    @Schema(description = "게시글 저장 여부. 이미 저장한 게시글이라면 <code>true</code>")
    val isSaved: Boolean,

    @Schema(description = "촬영 종류 리스트")
    val photographyTypes: Set<PhotographyType>,

    @Schema(description = "이미지 url 리스트", example = "[\"https://promotion-image\"]")
    val images: List<ImageResponse>,

    @Schema(description = "활동 지역 리스트")
    val activeRegions: Set<RegionResponse>,

    @Schema(description = "해시태그 리스트", example = "[\"수원핫플\", \"스냅사진\"]")
    val hashtags: Set<String>,
)
