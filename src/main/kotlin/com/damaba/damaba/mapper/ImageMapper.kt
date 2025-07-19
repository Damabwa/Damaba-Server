package com.damaba.damaba.mapper

import com.damaba.damaba.controller.common.ImageRequest
import com.damaba.damaba.controller.common.ImageResponse
import com.damaba.damaba.domain.file.Image
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

@Mapper
interface ImageMapper {
    fun toImageResponse(image: Image): ImageResponse

    fun toImage(profileImage: ImageRequest): Image

    companion object {
        val INSTANCE: ImageMapper = Mappers.getMapper(ImageMapper::class.java)
    }
}
