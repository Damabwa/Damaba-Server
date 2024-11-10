package com.damaba.damaba.adapter.inbound.promotion.dto

import com.damaba.damaba.adapter.inbound.common.dto.AddressRequest
import com.damaba.damaba.adapter.inbound.common.dto.FileRequest
import com.damaba.damaba.adapter.inbound.region.dto.RegionRequest
import com.damaba.damaba.application.port.inbound.promotion.PostPromotionUseCase
import com.damaba.damaba.domain.promotion.constant.EventType
import com.damaba.damaba.domain.promotion.constant.PromotionType
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

data class PostPromotionRequest(
    @Schema(description = "프로모션 종류")
    val type: PromotionType,

    @Schema(description = "이벤트 종류")
    val eventType: EventType,

    @Schema(description = "제목. 제목은 3~20 글자여야 합니다.", example = "이벤트 이름")
    val title: String,

    @Schema(description = "내용. 내용은 500 글자를 초과할 수 없습니다.", example = "이 이벤트는 오늘부터 시작해서...")
    val content: String,

    @Schema(description = "주소 정보")
    val address: AddressRequest,

    @Schema(description = "이벤트 관련 외부 링크", example = "https://promotion-instagram-post")
    val externalLink: String?,

    @Schema(description = "이벤트 시작일")
    val startedAt: LocalDate?,

    @Schema(description = "이벤트 종료일")
    val endedAt: LocalDate?,

    @Schema(description = "사진작가 이름", example = "담아사진")
    val photographerName: String?,

    @Schema(description = "사진작가 인스타 id", example = "dama.photo")
    val photographerInstagramId: String?,

    @Schema(description = "이미지 리스트. 이미지는 최소 1장부터 최대 10장까지 첨부할 수 있습니다.")
    val images: List<FileRequest>,

    @Schema(description = "활동 지역 리스트. 활동 지역은 최소 1개 이상 선택해야 합니다.")
    val activeRegions: Set<RegionRequest>,

    @Schema(description = "해시태그 리스트", example = "[\"수원핫플\", \"스냅사진\"]")
    val hashtags: Set<String>,
) {
    fun toCommand(requestUserId: Long) = PostPromotionUseCase.Command(
        authorId = requestUserId,
        type = type,
        eventType = eventType,
        title = title,
        content = content,
        address = address.toDomain(),
        externalLink = externalLink,
        startedAt = startedAt,
        endedAt = endedAt,
        photographerName = photographerName,
        photographerInstagramId = photographerInstagramId,
        images = images.map { fileReq -> fileReq.toDomain() },
        activeRegions = activeRegions.map { regionReq -> regionReq.toDomain() }.toSet(),
        hashtags = hashtags,
    )
}
