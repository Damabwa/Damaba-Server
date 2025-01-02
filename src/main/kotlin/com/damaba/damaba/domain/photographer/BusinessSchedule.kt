package com.damaba.damaba.domain.photographer

import java.time.DayOfWeek
import java.time.LocalTime

data class BusinessSchedule(
    val days: Set<DayOfWeek>,
    val startTime: LocalTime,
    val endTime: LocalTime,
)
