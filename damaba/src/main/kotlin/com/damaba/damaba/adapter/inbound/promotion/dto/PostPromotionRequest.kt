package com.damaba.damaba.adapter.inbound.promotion.dto

import com.damaba.common_exception.ValidationException
import com.damaba.common_file.domain.UploadFile
import com.damaba.damaba.application.port.inbound.promotion.PostPromotionUseCase
import com.damaba.damaba.domain.common.Address
import com.damaba.damaba.domain.promotion.constant.EventType
import com.damaba.damaba.domain.promotion.constant.PromotionType
import com.damaba.damaba.domain.region.Region
import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDate

data class PostPromotionRequest(
    @Schema(description = "프로모션 종류")
    val type: PromotionType,

    @Schema(description = "(프로모션 종류가 이벤트인 경우에만 해당) 이벤트 종류")
    val eventType: EventType?,

    @Schema(description = "제목. 제목은 3~20 글자여야 합니다.", example = "이벤트 이름")
    val title: String,

    @Schema(description = "내용. 내용은 500 글자를 초과할 수 없습니다.", example = "이 이벤트는 오늘부터 시작해서...")
    val content: String,

    @Schema(description = "시/도 이름", example = "경기")
    val sido: String,

    @Schema(description = "시/군/구 이름", example = "성남시 분당구")
    val sigungu: String,

    @Schema(description = "도로명 주소. 도로명 주소와 지번 주소 중 최소 하나는 입력되어야 합니다.", example = "경기 성남시 분당구 판교역로 166")
    val roadAddress: String,

    @Schema(description = "지번 주소. 도로명 주소와 지번 주소 중 최소 하나는 입력되어야 합니다.", example = "경기 성남시 분당구 백현동 532")
    val jibunAddress: String,

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
    val images: List<MultipartFile>,

    @Schema(
        description = "<p>활동 지역 리스트. 활동 지역은 최소 1개 이상 선택해야 합니다." +
            "<p>각 활동 지역은 <code>category</code>와 <code>name</code>으로 구성됩니다." +
            "<p><code>category</code>와 <code>name</code>는 공백으로 구분된 하나의 문자열로 요청해야 합니다. (예시 데이터 참고)",
        example = "[\"서울 강남구\", \"대전 서구\", \"경기 성남시 분당구\"]",
    )
    val activeRegions: Set<String>,

    @Schema(description = "해시태그 리스트", example = "[\"수원핫플\", \"스냅사진\"]")
    val hashtags: Set<String>,
) {
    fun toCommand(requestUserId: Long) = PostPromotionUseCase.Command(
        authorId = requestUserId,
        type = type,
        eventType = eventType,
        title = title,
        content = content,
        address = Address(sido, sigungu, roadAddress, jibunAddress),
        externalLink = externalLink,
        startedAt = startedAt,
        endedAt = endedAt,
        photographerName = photographerName,
        photographerInstagramId = photographerInstagramId,
        images = images.map { multipartFile ->
            UploadFile(
                name = multipartFile.originalFilename,
                size = multipartFile.size,
                contentType = multipartFile.contentType,
                inputStream = multipartFile.inputStream,
            )
        },
        activeRegions = activeRegions.map { regionString ->
            val firstSpaceIdx = regionString.indexOf(" ")
            if (firstSpaceIdx == -1) {
                throw ValidationException("전달된 activeRegions 요소가 잘못되었습니다. 형식은 \"category name\"이어야 합니다.")
            }
            val category = regionString.substring(0, firstSpaceIdx)
            val name = regionString.substring(firstSpaceIdx + 1)
            Region(category, name)
        }.toSet(),
        hashtags = hashtags,
    )
}
