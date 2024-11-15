package com.damaba.damaba.mapper

import com.damaba.common_file.domain.Image
import com.damaba.damaba.adapter.inbound.photographer.dto.PhotographerResponse
import com.damaba.damaba.adapter.outbound.photographer.PhotographerActiveRegionJpaEntity
import com.damaba.damaba.adapter.outbound.photographer.PhotographerJpaEntity
import com.damaba.damaba.adapter.outbound.photographer.PhotographerPortfolioImageJpaEntity
import com.damaba.damaba.domain.photographer.Photographer
import com.damaba.damaba.domain.region.Region
import com.damaba.user.adapter.outbound.user.UserJpaEntity
import org.mapstruct.AfterMapping
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingTarget
import org.mapstruct.factory.Mappers

@Mapper(uses = [AddressMapper::class, ImageMapper::class])
abstract class PhotographerMapper {
    abstract fun toPhotographerResponse(photographer: Photographer): PhotographerResponse

    @Mapping(source = "userJpaEntity.OAuthLoginUid", target = "oAuthLoginUid")
    @Mapping(source = "userJpaEntity.id", target = "id")
    abstract fun toPhotographer(
        userJpaEntity: UserJpaEntity,
        photographerJpaEntity: PhotographerJpaEntity,
    ): Photographer

    @Mapping(source = "id", target = "userId")
    @Mapping(target = "portfolio", ignore = true)
    @Mapping(target = "activeRegions", ignore = true)
    abstract fun toPhotographerJpaEntity(photographer: Photographer): PhotographerJpaEntity

    protected fun toImages(portfolio: List<PhotographerPortfolioImageJpaEntity>): List<Image> =
        portfolio.map { portfolioImage -> Image(portfolioImage.name, portfolioImage.url) }

    protected fun toRegions(activeRegionJpaEntities: Set<PhotographerActiveRegionJpaEntity>): Set<Region> =
        activeRegionJpaEntities.map { activeRegion -> Region(activeRegion.category, activeRegion.name) }.toSet()

    @AfterMapping
    protected fun addImagesToPhotographerPortfolio(
        @MappingTarget photographerJpaEntity: PhotographerJpaEntity,
        photographer: Photographer,
    ) {
        photographerJpaEntity.addPortfolioImages(
            photographer.portfolio.map { image ->
                PhotographerPortfolioImageJpaEntity(photographerJpaEntity, image.name, image.url)
            },
        )
    }

    @AfterMapping
    protected fun addRegionsToPhotographerJpaEntity(
        @MappingTarget photographerJpaEntity: PhotographerJpaEntity,
        photographer: Photographer,
    ) {
        photographerJpaEntity.activeRegions.addAll(
            photographer.activeRegions.map { region ->
                PhotographerActiveRegionJpaEntity(photographerJpaEntity, region.category, region.name)
            },
        )
    }

    companion object {
        val INSTANCE: PhotographerMapper = Mappers.getMapper(PhotographerMapper::class.java)
    }
}
