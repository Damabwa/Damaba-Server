package com.damaba.damaba.mapper

import com.damaba.common_file.domain.Image
import com.damaba.damaba.adapter.inbound.promotion.dto.PromotionResponse
import com.damaba.damaba.adapter.outbound.promotion.PromotionActiveRegionJpaEntity
import com.damaba.damaba.adapter.outbound.promotion.PromotionHashtagJpaEntity
import com.damaba.damaba.adapter.outbound.promotion.PromotionImageJpaEntity
import com.damaba.damaba.adapter.outbound.promotion.PromotionJpaEntity
import com.damaba.damaba.domain.promotion.Promotion
import com.damaba.damaba.domain.region.Region
import org.mapstruct.AfterMapping
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingTarget
import org.mapstruct.factory.Mappers

@Mapper(uses = [AddressMapper::class, ImageMapper::class, RegionMapper::class])
abstract class PromotionMapper {
    abstract fun toPromotionResponse(promotion: Promotion): PromotionResponse

    abstract fun toPromotion(promotionJpaEntity: PromotionJpaEntity): Promotion

    @Mapping(target = "images", ignore = true)
    @Mapping(target = "activeRegions", ignore = true)
    @Mapping(target = "hashtags", ignore = true)
    abstract fun toPromotionJpaEntity(promotion: Promotion): PromotionJpaEntity

    protected fun toImages(images: List<PromotionImageJpaEntity>): List<Image> =
        images.map { image -> Image(image.name, image.url) }

    protected fun toRegions(regions: Set<PromotionActiveRegionJpaEntity>): Set<Region> =
        regions.map { region -> Region(region.category, region.name) }.toSet()

    protected fun toStrings(hashtags: Set<PromotionHashtagJpaEntity>): Set<String> =
        hashtags.map { hashtag -> hashtag.content }.toSet()

    @AfterMapping
    protected fun addImagesToPromotionJpaEntity(
        @MappingTarget promotionJpaEntity: PromotionJpaEntity,
        promotion: Promotion,
    ) {
        promotionJpaEntity.addImages(
            promotion.images.map { image -> PromotionImageJpaEntity(promotionJpaEntity, image.name, image.url) },
        )
    }

    @AfterMapping
    protected fun addRegionsJpaEntitiesToPromotionJpaEntity(
        @MappingTarget promotionJpaEntity: PromotionJpaEntity,
        promotion: Promotion,
    ) {
        promotionJpaEntity.activeRegions.addAll(
            promotion.activeRegions.map { region ->
                PromotionActiveRegionJpaEntity(promotionJpaEntity, region.category, region.name)
            },
        )
    }

    @AfterMapping
    protected fun addHashtagsJpaEntitiesToPromotionJpaEntity(
        @MappingTarget promotionJpaEntity: PromotionJpaEntity,
        promotion: Promotion,
    ) {
        promotionJpaEntity.hashtags.addAll(
            promotion.hashtags.map { hashtag -> PromotionHashtagJpaEntity(promotionJpaEntity, hashtag) }.toSet(),
        )
    }

    companion object {
        val INSTANCE: PromotionMapper = Mappers.getMapper(PromotionMapper::class.java)
    }
}
