package com.damaba.damaba.adapter.outbound.promotion

import com.damaba.damaba.adapter.outbound.common.filterNotNull
import com.damaba.damaba.domain.common.PhotographyType
import com.damaba.damaba.domain.promotion.constant.PromotionProgressStatus
import com.damaba.damaba.domain.promotion.constant.PromotionSortType
import com.damaba.damaba.domain.promotion.constant.PromotionType
import com.damaba.damaba.domain.region.RegionFilterCondition
import com.linecorp.kotlinjdsl.querymodel.jpql.predicate.Predicate
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class PromotionJdslRepository(private val promotionJpaRepository: PromotionJpaRepository) {
    fun findPromotions(
        type: PromotionType?,
        progressStatus: PromotionProgressStatus?,
        regions: Set<RegionFilterCondition>,
        photographyTypes: Set<PhotographyType>,
        sortType: PromotionSortType,
        pageable: Pageable,
    ): Page<PromotionJpaEntity> {
        val result = promotionJpaRepository.findPage(pageable) {
            // Contions 생성
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

            // Query 생성
            selectDistinct(entity(PromotionJpaEntity::class))
                .from(
                    entity(PromotionJpaEntity::class),
                    leftJoin(PromotionJpaEntity::activeRegions).`as`(entity(PromotionActiveRegionJpaEntity::class)),
                    leftJoin(PromotionJpaEntity::photographyTypes).`as`(entity(PromotionPhotographyTypeJpaEntity::class)),
                )
                .whereAnd(*conditions.toTypedArray())
                .orderBy(
                    when (sortType) {
                        PromotionSortType.LATEST -> path(PromotionJpaEntity::createdAt).desc()
                        PromotionSortType.POPULAR -> path(PromotionJpaEntity::viewCount).desc()
                    },
                )
        }
        val resultNonNull = result.filterNotNull()
        return resultNonNull
    }
}
