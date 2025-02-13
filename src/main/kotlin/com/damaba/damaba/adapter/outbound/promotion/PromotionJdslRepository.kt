package com.damaba.damaba.adapter.outbound.promotion

import com.damaba.damaba.adapter.outbound.common.filterNotNull
import com.damaba.damaba.adapter.outbound.user.UserJpaEntity
import com.damaba.damaba.domain.common.PhotographyType
import com.damaba.damaba.domain.promotion.PromotionListItem
import com.damaba.damaba.domain.promotion.constant.PromotionProgressStatus
import com.damaba.damaba.domain.promotion.constant.PromotionSortType
import com.damaba.damaba.domain.promotion.constant.PromotionType
import com.damaba.damaba.domain.region.RegionFilterCondition
import com.damaba.damaba.mapper.PromotionMapper
import com.damaba.damaba.mapper.UserMapper
import com.linecorp.kotlinjdsl.querymodel.jpql.predicate.Predicate
import jakarta.persistence.Tuple
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class PromotionJdslRepository(private val promotionJpaRepository: PromotionJpaRepository) {
    fun findPromotionList(
        requestUserId: Long?,
        type: PromotionType?,
        progressStatus: PromotionProgressStatus?,
        regions: Set<RegionFilterCondition>,
        photographyTypes: Set<PhotographyType>,
        sortType: PromotionSortType,
        pageable: Pageable,
    ): Page<PromotionListItem> {
        val result = promotionJpaRepository.findPage(pageable) {
            val conditions = mutableListOf<Predicate>()

            // 삭제된 프로모션 제외
            conditions += path(PromotionJpaEntity::deletedAt).isNull()

            // 프로모션 종류 필터링
            if (type != null) {
                conditions += path(PromotionJpaEntity::promotionType).eq(type)
            }

            // 진행 상태 필터링
            if (progressStatus != null) {
                val today = LocalDate.now()
                conditions += when (progressStatus) {
                    PromotionProgressStatus.UPCOMING ->
                        path(PromotionJpaEntity::startedAt).gt(today)

                    PromotionProgressStatus.ONGOING ->
                        path(PromotionJpaEntity::startedAt).le(today)
                            .and(path(PromotionJpaEntity::endedAt).ge(today))

                    PromotionProgressStatus.ENDED ->
                        path(PromotionJpaEntity::endedAt).lt(today)
                }
            }

            // 촬영 종류 필터링
            if (photographyTypes.isNotEmpty()) {
                conditions += path(PromotionPhotographyTypeJpaEntity::type).`in`(photographyTypes)
            }

            // 지역 필터링
            if (regions.isNotEmpty()) {
                val regionConditions = regions.map { region ->
                    var regionCond = path(PromotionActiveRegionJpaEntity::category).eq(region.category)
                    if (region.name != null) {
                        regionCond = regionCond.and(path(PromotionActiveRegionJpaEntity::name).eq(region.name))
                    }
                    return@map regionCond
                }
                conditions += or(*regionConditions.toTypedArray())
            }

            val saveCountQuery = select(count(SavedPromotionJpaEntity::id))
                .from(entity(SavedPromotionJpaEntity::class))
                .where(path(SavedPromotionJpaEntity::promotionId).eq(path(PromotionJpaEntity::id)))
                .asSubquery()

            val isSavedQuery = select(path(SavedPromotionJpaEntity::id))
                .from(entity(SavedPromotionJpaEntity::class))
                .whereAnd(
                    path(SavedPromotionJpaEntity::promotionId).eq(path(PromotionJpaEntity::id)),
                    path(SavedPromotionJpaEntity::userId).eq(requestUserId),
                ).asSubquery()

            // Query 생성
            val saveCount = expression(Long::class, "saveCount")
            val isSaved = expression(Boolean::class, "isSaved")
            selectDistinct<Tuple>(
                entity(PromotionJpaEntity::class),
                entity(UserJpaEntity::class),
                saveCountQuery.`as`(saveCount),
                exists(isSavedQuery).`as`(isSaved),
            ).from(
                entity(PromotionJpaEntity::class),
                leftFetchJoin(UserJpaEntity::class)
                    .on(path(PromotionJpaEntity::authorId).eq(path(UserJpaEntity::id))),
                leftJoin(PromotionPhotographyTypeJpaEntity::class).on(
                    path(PromotionPhotographyTypeJpaEntity::promotion)(PromotionJpaEntity::id)
                        .eq(path(PromotionJpaEntity::id)),
                ),
                leftJoin(PromotionActiveRegionJpaEntity::class).on(
                    path(PromotionActiveRegionJpaEntity::promotion)(PromotionJpaEntity::id)
                        .eq(path(PromotionJpaEntity::id)),
                ),
            ).whereAnd(
                *conditions.toTypedArray(),
            ).orderBy(
                when (sortType) {
                    PromotionSortType.LATEST -> path(PromotionJpaEntity::createdAt).desc()
                    PromotionSortType.POPULAR -> path(PromotionJpaEntity::viewCount).desc()
                },
            )
        }
        return result.filterNotNull().map { tuple ->
            PromotionMapper.INSTANCE.toPromotionListItem(
                promotion = PromotionMapper.INSTANCE.toPromotion(tuple.get(0) as PromotionJpaEntity),
                author = tuple.get(1)?.let { UserMapper.INSTANCE.toUser(it as UserJpaEntity) },
                saveCount = tuple.get(2) as Long,
                isSaved = tuple.get(3) as Boolean,
            )
        }
    }
}
