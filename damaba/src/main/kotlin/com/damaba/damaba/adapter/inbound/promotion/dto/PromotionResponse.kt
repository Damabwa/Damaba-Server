package com.damaba.damaba.adapter.inbound.promotion.dto

import com.damaba.damaba.adapter.inbound.common.dto.AddressResponse
import com.damaba.damaba.adapter.inbound.common.dto.FileResponse
import com.damaba.damaba.domain.promotion.Promotion
import com.damaba.damaba.domain.promotion.constant.EventType
import com.damaba.damaba.domain.promotion.constant.PromotionType
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

data class PromotionResponse(
    @Schema(description = "Id of the promotion")
    val id: Long,

    @Schema(description = "프로모션 작성자의 id")
    val authorId: Long?,

    @Schema(description = "프로모션 종류")
    val type: PromotionType,

    @Schema(description = "이벤트 종류. 프로모션 종류가 이벤트가 아닌 경우에는 null")
    val eventType: EventType?,

    @Schema(description = "제목", example = "이벤트 이름")
    val title: String,

    @Schema(description = "내용", example = "이 이벤트는 오늘부터 시작해서...")
    val content: String,

    @Schema(description = "주소")
    val address: AddressResponse,

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

    @Schema(description = "이미지 url 리스트", example = "[\"https://promotion-image\"]")
    val images: List<FileResponse>,

    @Schema(description = "활동 지역 리스트")
    val activeRegions: Set<PromotionActiveRegionResponse>,

    @Schema(description = "해시태그 리스트", example = "[\"수원핫플\", \"스냅사진\"]")
    val hashtags: Set<String>,
) {
    companion object {
        fun from(promotion: Promotion) = PromotionResponse(
            id = promotion.id,
            authorId = promotion.authorId,
            type = promotion.type,
            eventType = promotion.eventType,
            title = promotion.title,
            content = promotion.content,
            address = AddressResponse.from(promotion.address),
            externalLink = promotion.externalLink,
            startedAt = promotion.startedAt,
            endedAt = promotion.endedAt,
            photographerName = promotion.photographerName,
            photographerInstagramId = promotion.photographerInstagramId,
            images = promotion.images.map { image -> FileResponse(image.name, image.url) },
            activeRegions = promotion.activeRegions.map { PromotionActiveRegionResponse.from(it) }.toSet(),
            hashtags = promotion.hashtags,
        )
    }
}
