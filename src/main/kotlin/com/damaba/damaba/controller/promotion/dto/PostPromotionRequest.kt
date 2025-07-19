package com.damaba.damaba.controller.promotion.dto

import com.damaba.damaba.application.promotion.PostPromotionCommand
import com.damaba.damaba.controller.common.dto.ImageRequest
import com.damaba.damaba.controller.region.dto.RegionRequest
import com.damaba.damaba.domain.common.constant.PhotographyType
import com.damaba.damaba.domain.promotion.constant.PromotionType
import com.damaba.damaba.mapper.ImageMapper
import com.damaba.damaba.mapper.RegionMapper
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

data class PostPromotionRequest(
    @Schema(description = "이벤트 종류")
    val promotionType: PromotionType,

    @Schema(description = "제목. 제목은 3~20 글자여야 합니다.", example = "이벤트 이름")
    val title: String,

    @Schema(description = "내용. 내용은 500 글자를 초과할 수 없습니다.", example = "이 이벤트는 오늘부터 시작해서...")
    val content: String,

    @Schema(description = "이벤트 관련 외부 링크", example = "https://promotion-instagram-post")
    val externalLink: String?,

    @Schema(description = "이벤트 시작일")
    val startedAt: LocalDate?,

    @Schema(description = "이벤트 종료일")
    val endedAt: LocalDate?,

    @Schema(description = "촬영 종류")
    val photographyTypes: Set<PhotographyType>,

    @Schema(description = "이미지 리스트. 이미지는 최소 1장부터 최대 10장까지 첨부할 수 있습니다.")
    val images: List<ImageRequest>,

    @Schema(description = "활동 지역 리스트. 활동 지역은 최소 1개 이상 선택해야 합니다.")
    val activeRegions: Set<RegionRequest>,

    @Schema(description = "해시태그 리스트", example = "[\"수원핫플\", \"스냅사진\"]")
    val hashtags: Set<String>,
) {
    fun toCommand(requestUserId: Long) = PostPromotionCommand(
        authorId = requestUserId,
        promotionType = promotionType,
        title = title,
        content = content,
        externalLink = externalLink,
        startedAt = startedAt,
        endedAt = endedAt,
        photographyTypes = photographyTypes,
        images = images.map { ImageMapper.INSTANCE.toImage(it) },
        activeRegions = activeRegions.map { regionRequest -> RegionMapper.INSTANCE.toRegion(regionRequest) }.toSet(),
        hashtags = hashtags,
    )
}
