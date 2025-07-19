package com.damaba.damaba.mapper

import com.damaba.damaba.controller.promotion.PromotionDetailResponse
import com.damaba.damaba.controller.promotion.PromotionListItemResponse
import com.damaba.damaba.controller.promotion.PromotionResponse
import com.damaba.damaba.domain.promotion.Promotion
import com.damaba.damaba.domain.promotion.PromotionDetail
import com.damaba.damaba.domain.promotion.PromotionListItem
import com.damaba.damaba.domain.user.User
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.factory.Mappers

@Mapper(uses = [UserMapper::class, ImageMapper::class, RegionMapper::class])
interface PromotionMapper {
    fun toPromotionResponse(promotion: Promotion): PromotionResponse

    @Mapping(source = "saved", target = "isSaved")
    fun toPromotionDetailResponse(promotionDetail: PromotionDetail): PromotionDetailResponse

    @Mapping(source = "saved", target = "isSaved")
    fun toPromotionListItemResponse(promotionListItem: PromotionListItem): PromotionListItemResponse

    @Mapping(source = "promotion.id", target = "id")
    fun toPromotionDetail(
        promotion: Promotion,
        author: User?,
        saveCount: Long,
        isSaved: Boolean,
    ): PromotionDetail

    @Mapping(source = "promotion.id", target = "id")
    fun toPromotionListItem(
        promotion: Promotion,
        author: User?,
        saveCount: Long,
        isSaved: Boolean,
    ): PromotionListItem

    companion object {
        val INSTANCE: PromotionMapper = Mappers.getMapper(PromotionMapper::class.java)
    }
}
