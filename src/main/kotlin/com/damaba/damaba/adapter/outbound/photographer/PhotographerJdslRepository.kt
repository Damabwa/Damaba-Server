package com.damaba.damaba.adapter.outbound.photographer

import com.damaba.damaba.adapter.outbound.common.filterNotNull
import com.damaba.damaba.adapter.outbound.user.UserJpaEntity
import com.damaba.damaba.domain.common.PhotographyType
import com.damaba.damaba.domain.photographer.PhotographerListItem
import com.damaba.damaba.domain.photographer.constant.PhotographerSortType
import com.damaba.damaba.domain.region.RegionFilterCondition
import com.damaba.damaba.mapper.PhotographerMapper
import com.linecorp.kotlinjdsl.querymodel.jpql.predicate.Predicate
import jakarta.persistence.Tuple
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class PhotographerJdslRepository(private val photographerJpaRepository: PhotographerJpaRepository) {
    fun findPhotographerList(
        reqUserId: Long?,
        regions: Set<RegionFilterCondition>,
        photographyTypes: Set<PhotographyType>,
        sort: PhotographerSortType,
        pageable: Pageable,
    ): Page<PhotographerListItem> {
        val tuples = photographerJpaRepository.findPage(pageable) {
            val conditions = mutableListOf<Predicate>()

            // 지역 필터링
            if (regions.isNotEmpty()) {
                val regionConditions = regions.map { region ->
                    var regionCond = path(PhotographerActiveRegionJpaEntity::category).eq(region.category)
                    if (region.name != null) {
                        regionCond = regionCond.and(path(PhotographerActiveRegionJpaEntity::name).eq(region.name))
                    }
                    return@map regionCond
                }
                conditions += or(*regionConditions.toTypedArray())
            }

            // 촬영 종류 필터링
            if (photographyTypes.isNotEmpty()) {
                conditions += path(PhotographerPhotographyTypeJpaEntity::photographyType).`in`(photographyTypes)
            }

            val saveCountQuery = select(count(SavedPhotographerJpaEntity::id))
                .from(entity(SavedPhotographerJpaEntity::class))
                .where(path(SavedPhotographerJpaEntity::photographerId).eq(path(PhotographerJpaEntity::userId)))
                .asSubquery()

            val isSavedQuery = select(path(SavedPhotographerJpaEntity::id))
                .from(entity(SavedPhotographerJpaEntity::class))
                .whereAnd(
                    path(SavedPhotographerJpaEntity::photographerId).eq(path(PhotographerJpaEntity::userId)),
                    path(SavedPhotographerJpaEntity::userId).eq(reqUserId),
                ).asSubquery()

            val saveCount = expression(Long::class, "saveCount")
            val isSaved = expression(Boolean::class, "isSaved")
            selectDistinct<Tuple>(
                entity(PhotographerJpaEntity::class),
                entity(UserJpaEntity::class),
                saveCountQuery.`as`(saveCount),
                exists(isSavedQuery).`as`(isSaved),
            ).from(
                entity(PhotographerJpaEntity::class),
                fetchJoin(UserJpaEntity::class).on(
                    path(PhotographerJpaEntity::userId).eq(path(UserJpaEntity::id)),
                ),
                leftJoin(PhotographerActiveRegionJpaEntity::class).on(
                    path(PhotographerActiveRegionJpaEntity::photographer)(PhotographerJpaEntity::userId)
                        .eq(path(PhotographerJpaEntity::userId)),
                ),
                leftJoin(PhotographerPhotographyTypeJpaEntity::class).on(
                    path(PhotographerPhotographyTypeJpaEntity::photographer)(PhotographerJpaEntity::userId)
                        .eq(path(PhotographerJpaEntity::userId)),
                ),
            ).whereAnd(
                *conditions.toTypedArray(),
            ).orderBy(
                when (sort) {
                    PhotographerSortType.LATEST -> path(PhotographerJpaEntity::createdAt).desc()
                    PhotographerSortType.POPULAR -> saveCount.desc()
                },
            )
        }

        return tuples.filterNotNull().map { tuple ->
            PhotographerMapper.INSTANCE.toPhotographerListItem(
                photographer = (tuple.get(0) as PhotographerJpaEntity).toPhotographer(tuple.get(1) as UserJpaEntity),
                isSaved = tuple.get(3) as Boolean,
            )
        }
    }
}
