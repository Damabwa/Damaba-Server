package com.damaba.damaba.adapter.inbound.photographer.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.time.DayOfWeek
import java.time.LocalTime

data class BusinessScheduleResponse(
    @Schema(description = "영업 요일")
    val days: Set<DayOfWeek>,

    @Schema(description = "영업 시작 시간")
    val startTime: LocalTime,

    @Schema(description = "영업 종료 시간")
    val endTime: LocalTime,
)
