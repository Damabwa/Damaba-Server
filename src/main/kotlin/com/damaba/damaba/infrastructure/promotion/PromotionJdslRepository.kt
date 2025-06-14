package com.damaba.damaba.infrastructure.promotion

import com.damaba.damaba.domain.common.constant.PhotographyType
import com.damaba.damaba.domain.promotion.PromotionListItem
import com.damaba.damaba.domain.promotion.constant.PromotionProgressStatus
import com.damaba.damaba.domain.promotion.constant.PromotionSortType
import com.damaba.damaba.domain.promotion.constant.PromotionType
import com.damaba.damaba.domain.region.RegionFilterCondition
import com.damaba.damaba.infrastructure.common.filterNotNull
import com.damaba.damaba.infrastructure.user.UserJpaEntity
import com.damaba.damaba.mapper.PromotionMapper
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
        searchKeyword: String?,
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
                        regionCond =
                            regionCond.and(path(PromotionActiveRegionJpaEntity::name).like("%${region.name}%"))
                    }
                    return@map regionCond
                }
                conditions += or(*regionConditions.toTypedArray())
            }

            // 검색 기능 반영
            if (searchKeyword != null) {
                conditions += path(PromotionJpaEntity::title).like("%$searchKeyword%")
                    .or(path(PromotionJpaEntity::content).like("%$searchKeyword%"))
                    .or(path(PromotionActiveRegionJpaEntity::category).like("%$searchKeyword%"))
                    .or(path(PromotionActiveRegionJpaEntity::name).like("%$searchKeyword%"))
                    .or(path(PromotionHashtagJpaEntity::content).like("%$searchKeyword%"))
            }

            val saveCountQuery = select(count(PromotionSaveJpaEntity::id))
                .from(entity(PromotionSaveJpaEntity::class))
                .where(path(PromotionSaveJpaEntity::promotionId).eq(path(PromotionJpaEntity::id)))
                .asSubquery()

            val isSavedQuery = select(path(PromotionSaveJpaEntity::id))
                .from(entity(PromotionSaveJpaEntity::class))
                .whereAnd(
                    path(PromotionSaveJpaEntity::promotionId).eq(path(PromotionJpaEntity::id)),
                    path(PromotionSaveJpaEntity::userId).eq(requestUserId),
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
                leftJoin(PromotionHashtagJpaEntity::class).on(
                    path(PromotionHashtagJpaEntity::promotion)(PromotionJpaEntity::id)
                        .eq(path(PromotionJpaEntity::id)),
                ),
            ).whereAnd(
                *conditions.toTypedArray(),
            ).orderBy(
                when (sortType) {
                    PromotionSortType.LATEST -> path(PromotionJpaEntity::createdAt).desc()
                    PromotionSortType.POPULAR -> saveCount.desc()
                },
            )
        }
        return result.filterNotNull().map { tuple ->
            PromotionMapper.INSTANCE.toPromotionListItem(
                promotion = (tuple.get(0) as PromotionJpaEntity).toPromotion(),
                author = tuple.get(1)?.let { (it as UserJpaEntity).toUser() },
                saveCount = tuple.get(2) as Long,
                isSaved = tuple.get(3) as Boolean,
            )
        }
    }

    fun findSavedPromotionList(requestUserId: Long, pageable: Pageable): Page<PromotionListItem> {
        val result = promotionJpaRepository.findPage(pageable) {
            val saveCountQuery = select(count(PromotionSaveJpaEntity::id))
                .from(entity(PromotionSaveJpaEntity::class))
                .where(path(PromotionSaveJpaEntity::promotionId).eq(path(PromotionJpaEntity::id)))
                .asSubquery()

            // Query 생성
            selectDistinct<Tuple>(
                entity(PromotionJpaEntity::class),
                entity(UserJpaEntity::class),
                path(PromotionSaveJpaEntity::createdAt),
                saveCountQuery.`as`(expression(Long::class, "saveCount")),
            ).from(
                entity(PromotionSaveJpaEntity::class),
                fetchJoin(PromotionJpaEntity::class)
                    .on(path(PromotionSaveJpaEntity::promotionId).eq(path(PromotionJpaEntity::id))),
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
                path(PromotionSaveJpaEntity::userId).eq(requestUserId),
            ).orderBy(
                path(PromotionSaveJpaEntity::createdAt).desc(),
            )
        }
        return result.filterNotNull().map { tuple ->
            PromotionMapper.INSTANCE.toPromotionListItem(
                promotion = (tuple.get(0) as PromotionJpaEntity).toPromotion(),
                author = tuple.get(1)?.let { (it as UserJpaEntity).toUser() },
                saveCount = tuple.get(3) as Long,
                isSaved = true,
            )
        }
    }
}
