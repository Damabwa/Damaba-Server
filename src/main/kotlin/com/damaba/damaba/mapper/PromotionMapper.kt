package com.damaba.damaba.mapper

import com.damaba.damaba.adapter.inbound.promotion.dto.PromotionDetailResponse
import com.damaba.damaba.adapter.inbound.promotion.dto.PromotionResponse
import com.damaba.damaba.adapter.outbound.promotion.PromotionActiveRegionJpaEntity
import com.damaba.damaba.adapter.outbound.promotion.PromotionHashtagJpaEntity
import com.damaba.damaba.adapter.outbound.promotion.PromotionImageJpaEntity
import com.damaba.damaba.adapter.outbound.promotion.PromotionJpaEntity
import com.damaba.damaba.adapter.outbound.promotion.PromotionPhotographyTypeJpaEntity
import com.damaba.damaba.adapter.outbound.promotion.SavedPromotionJpaEntity
import com.damaba.damaba.domain.common.PhotographyType
import com.damaba.damaba.domain.file.Image
import com.damaba.damaba.domain.promotion.Promotion
import com.damaba.damaba.domain.promotion.PromotionDetail
import com.damaba.damaba.domain.promotion.SavedPromotion
import com.damaba.damaba.domain.region.Region
import com.damaba.damaba.domain.user.User
import org.mapstruct.AfterMapping
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingTarget
import org.mapstruct.factory.Mappers

@Mapper(uses = [UserMapper::class, AddressMapper::class, ImageMapper::class, RegionMapper::class])
abstract class PromotionMapper {
    abstract fun toPromotionResponse(promotion: Promotion): PromotionResponse

    @Mapping(source = "saved", target = "isSaved")
    abstract fun toPromotionDetailResponse(promotionDetail: PromotionDetail): PromotionDetailResponse

    abstract fun toPromotion(promotionJpaEntity: PromotionJpaEntity): Promotion

    @Mapping(source = "promotion.id", target = "id")
    abstract fun toPromotionDetail(
        promotion: Promotion,
        author: User?,
        saveCount: Int,
        isSaved: Boolean,
    ): PromotionDetail

    abstract fun toSavedPromotion(savedPromotionJpaEntity: SavedPromotionJpaEntity): SavedPromotion

    @Mapping(target = "photographyTypes", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "activeRegions", ignore = true)
    @Mapping(target = "hashtags", ignore = true)
    abstract fun toPromotionJpaEntity(promotion: Promotion): PromotionJpaEntity

    protected fun toPhotographyTypes(
        photographyTypeJpaEntities: Set<PromotionPhotographyTypeJpaEntity>,
    ): Set<PhotographyType> = photographyTypeJpaEntities.map { it.type }.toSet()

    protected fun toImages(
        imageJpaEntities: List<PromotionImageJpaEntity>,
    ): List<Image> = imageJpaEntities.map { image -> Image(image.name, image.url) }

    protected fun toRegions(
        regionJpaEntities: Set<PromotionActiveRegionJpaEntity>,
    ): Set<Region> = regionJpaEntities.map { region -> Region(region.category, region.name) }.toSet()

    protected fun toHashtags(
        hashtagJpaEntities: Set<PromotionHashtagJpaEntity>,
    ): Set<String> = hashtagJpaEntities.map { hashtag -> hashtag.content }.toSet()

    @AfterMapping
    protected fun mapJpaEntities(
        @MappingTarget promotionJpaEntity: PromotionJpaEntity,
        promotion: Promotion,
    ) {
        promotionJpaEntity.photographyTypes.addAll(
            promotion.photographyTypes.map { PromotionPhotographyTypeJpaEntity(promotionJpaEntity, it) },
        )
        promotionJpaEntity.addImages(
            promotion.images.map { PromotionImageJpaEntity(promotionJpaEntity, it.name, it.url) },
        )
        promotionJpaEntity.activeRegions.addAll(
            promotion.activeRegions.map { PromotionActiveRegionJpaEntity(promotionJpaEntity, it.category, it.name) },
        )
        promotionJpaEntity.hashtags.addAll(
            promotion.hashtags.map { PromotionHashtagJpaEntity(promotionJpaEntity, it) }.toSet(),
        )
    }

    companion object {
        val INSTANCE: PromotionMapper = Mappers.getMapper(PromotionMapper::class.java)
    }
}
