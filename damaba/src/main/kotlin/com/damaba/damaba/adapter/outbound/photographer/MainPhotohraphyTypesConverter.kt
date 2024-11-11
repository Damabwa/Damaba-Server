package com.damaba.damaba.adapter.outbound.photographer

import com.damaba.damaba.domain.common.PhotographyType
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import java.util.stream.Collectors

@Converter
class MainPhotohraphyTypesConverter : AttributeConverter<Set<PhotographyType>, String> {
    override fun convertToDatabaseColumn(attribute: Set<PhotographyType>): String =
        attribute.stream()
            .map { roleType -> roleType.name }
            .sorted()
            .collect(Collectors.joining(DELIMITER))

    override fun convertToEntityAttribute(dbData: String): Set<PhotographyType> =
        dbData.split(DELIMITER)
            .map { roleType -> PhotographyType.valueOf(roleType) }
            .toSet()

    companion object {
        private const val DELIMITER = ","
    }
}
