package com.damaba.damaba.adapter.outbound.photographer

import com.damaba.damaba.domain.photographer.BusinessSchedule
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Embeddable
import java.time.DayOfWeek
import java.time.LocalTime

@Embeddable
data class BusinessScheduleJpaEmbeddable(
    @Convert(converter = BusinessDaysConverter::class)
    @Column(name = "business_days", nullable = true)
    val days: Set<DayOfWeek>,

    @Column(name = "business_start_time", nullable = true)
    val startTime: LocalTime,

    @Column(name = "business_end_time", nullable = true)
    val endTime: LocalTime,
) {
    fun toBusinessSchedule() = BusinessSchedule(
        days = this.days,
        startTime = this.startTime,
        endTime = this.endTime,
    )

    companion object {
        fun from(businessSchedule: BusinessSchedule) = BusinessScheduleJpaEmbeddable(
            days = businessSchedule.days,
            startTime = businessSchedule.startTime,
            endTime = businessSchedule.endTime,
        )
    }
}
