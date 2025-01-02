package com.damaba.damaba.adapter.outbound.photographer

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import java.time.DayOfWeek

@Converter
class BusinessDaysConverter : AttributeConverter<Set<DayOfWeek>, String> {
    override fun convertToDatabaseColumn(attributes: Set<DayOfWeek>?): String? =
        attributes
            ?.sortedBy { it.value }
            ?.joinToString(DELIMITER) { it.name }

    override fun convertToEntityAttribute(dbData: String?): Set<DayOfWeek>? =
        dbData?.split(DELIMITER)
            ?.map { DayOfWeek.valueOf(it) }
            ?.sortedBy { it.value }
            ?.toSet()

    companion object {
        private const val DELIMITER = ","
    }
}
