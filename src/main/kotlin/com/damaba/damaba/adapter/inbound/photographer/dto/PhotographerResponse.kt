package com.damaba.damaba.adapter.inbound.photographer.dto

import com.damaba.damaba.adapter.inbound.common.dto.AddressResponse
import com.damaba.damaba.adapter.inbound.common.dto.ImageResponse
import com.damaba.damaba.adapter.inbound.region.dto.RegionResponse
import com.damaba.damaba.domain.common.PhotographyType
import com.damaba.user.domain.user.constant.Gender
import com.damaba.user.domain.user.constant.LoginType
import com.damaba.user.domain.user.constant.UserType
import io.swagger.v3.oas.annotations.media.Schema

data class PhotographerResponse(
    @Schema(description = "Id of user", example = "1")
    val id: Long,

    @Schema(description = "User type")
    val type: UserType,

    @Schema(description = "사용하는 로그인 종류")
    val loginType: LoginType,

    @Schema(description = "닉네임", example = "홍길동")
    val nickname: String,

    @Schema(description = "프로필 이미지")
    val profileImage: ImageResponse,

    @Schema(description = "성별")
    val gender: Gender,

    @Schema(description = "(Nullable) 인스타 아이디", example = "damaba.official")
    val instagramId: String?,

    @Schema(description = "주력 촬영 종류")
    val mainPhotographyTypes: Set<PhotographyType>,

    @Schema(description = "(Nullable) 대표 링크", example = "https://damaba-contact.com")
    val contactLink: String?,

    @Schema(description = "(Nullable) 상세 소개", example = "안녕하세요. 수원에서 주로...")
    val description: String?,

    @Schema(description = "(Nullable) 상세주소")
    val address: AddressResponse?,

    @Schema(description = "(Nullable) 영업시간")
    val businessSchedule: BusinessScheduleResponse?,

    @Schema(description = "포트폴리오")
    val portfolio: List<ImageResponse>,

    @Schema(description = "활동 지역")
    val activeRegions: Set<RegionResponse>,
)
