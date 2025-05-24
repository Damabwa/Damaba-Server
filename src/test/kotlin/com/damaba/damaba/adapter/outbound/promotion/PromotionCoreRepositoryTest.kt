package com.damaba.damaba.adapter.outbound.promotion

import com.damaba.damaba.adapter.outbound.user.UserCoreRepository
import com.damaba.damaba.config.JpaConfig
import com.damaba.damaba.domain.common.constant.PhotographyType
import com.damaba.damaba.domain.promotion.PromotionSave
import com.damaba.damaba.domain.promotion.constant.PromotionProgressStatus
import com.damaba.damaba.domain.promotion.constant.PromotionSortType
import com.damaba.damaba.domain.promotion.constant.PromotionType
import com.damaba.damaba.domain.promotion.exception.PromotionNotFoundException
import com.damaba.damaba.domain.region.Region
import com.damaba.damaba.domain.region.RegionFilterCondition
import com.damaba.damaba.util.RandomTestUtils.Companion.randomLong
import com.damaba.damaba.util.fixture.PromotionFixture.createPromotion
import com.damaba.damaba.util.fixture.RegionFixture.createRegion
import com.damaba.damaba.util.fixture.UserFixture.createUser
import com.linecorp.kotlinjdsl.support.spring.data.jpa.autoconfigure.KotlinJdslAutoConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIterable
import org.assertj.core.api.Assertions.catchThrowable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate
import kotlin.test.Test

@ActiveProfiles("test")
@Import(
    JpaConfig::class,
    KotlinJdslAutoConfiguration::class,
    UserCoreRepository::class,
    PromotionCoreRepository::class,
    PromotionJdslRepository::class,
    PromotionSaveCoreRepository::class,
)
@DataJpaTest
class PromotionCoreRepositoryTest @Autowired constructor(
    private val promotionCoreRepository: PromotionCoreRepository,
    private val promotionSaveCoreRepository: PromotionSaveCoreRepository,
) {
    @Autowired
    private lateinit var promotionJpaRepository: PromotionJpaRepository

    @Autowired
    private lateinit var userCoreRepository: UserCoreRepository

    @Test
    fun `(Get) 프로모션 id가 주어지고, 주어진 id와 일치하는 프로모션을 단건 조회하면, 조회된 프로모션이 반환된다`() {
        // given
        val promotion = promotionCoreRepository.create(createPromotion())

        // when
        val result = promotionCoreRepository.getById(promotion.id)

        // then
        assertThat(result).isEqualTo(promotion)
    }

    @Test
    fun `(Get) 프로모션 id가 주어지고, 주어진 id와 일치하는 프로모션을 단건 조회한다, 만약 일치하는 프로모션이 없다면 예외가 발생한다`() {
        // given

        // when
        val ex = catchThrowable { promotionCoreRepository.getById(1L) }

        // then
        assertThat(ex).isInstanceOf(PromotionNotFoundException::class.java)
    }

    @Test
    fun `필터링 조건이 주어지고, 프로모션 리스트를 조회하면, 주어진 조건에 맞는 프로모션 리스트가 반환된다`() {
        // given
        val reqUserId = randomLong()

        val freePromotion = promotionCoreRepository.create(
            createPromotion(
                promotionType = PromotionType.FREE,
                startedAt = LocalDate.now().minusDays(2),
                endedAt = LocalDate.now().plusDays(2),
                photographyTypes = setOf(PhotographyType.SNAP),
                activeRegions = setOf(createRegion(category = "RegionA", name = "CityA")),
            ),
        )
        promotionSaveCoreRepository.create(PromotionSave.create(userId = reqUserId, promotionId = freePromotion.id))

        promotionCoreRepository.create(
            createPromotion(
                promotionType = PromotionType.DISCOUNT,
                startedAt = LocalDate.now().plusDays(1),
                endedAt = LocalDate.now().plusDays(5),
                photographyTypes = setOf(PhotographyType.SNAP),
                activeRegions = setOf(Region(category = "RegionB", name = "CityB")),
            ),
        )

        val type = PromotionType.FREE
        val progressStatus = PromotionProgressStatus.ONGOING
        val regions = setOf(
            RegionFilterCondition(category = "RegionA", name = "CityA"),
            RegionFilterCondition(category = "RegionB", name = null),
        )
        val photographyTypes = setOf(PhotographyType.SNAP)
        val sortType = PromotionSortType.LATEST
        val page = 0
        val pageSize = 10

        // when
        val promotions = promotionCoreRepository.findPromotionList(
            requestUserId = reqUserId,
            type = type,
            progressStatus = progressStatus,
            regions = regions,
            photographyTypes = photographyTypes,
            sortType = sortType,
            page = page,
            pageSize = pageSize,
        )

        // then
        assertThat(promotions.items).hasSize(1)
        assertThat(promotions.items.first().saveCount).isEqualTo(1)
        assertThat(promotions.items.first().isSaved).isTrue()
        assertThat(promotions.page).isEqualTo(page)
        assertThat(promotions.pageSize).isEqualTo(pageSize)
    }

    @Test
    fun `필터링 조건 없이 프로모션 리스트를 조회하면, 모든 프로모션이 반환된다`() {
        // given
        val author = userCoreRepository.create(createUser())
        promotionCoreRepository.create(createPromotion(authorId = author.id))
        promotionCoreRepository.create(createPromotion(authorId = author.id))
        promotionCoreRepository.create(createPromotion(authorId = null))

        val page = 0
        val pageSize = 10

        // when
        val promotions = promotionCoreRepository.findPromotionList(
            requestUserId = null,
            type = null,
            progressStatus = null,
            regions = emptySet(),
            photographyTypes = emptySet(),
            sortType = PromotionSortType.POPULAR,
            page = page,
            pageSize = pageSize,
        )

        // then
        assertThat(promotions.items).hasSize(3)
        assertThat(promotions.items.first().saveCount).isEqualTo(0)
        assertThat(promotions.items.first().isSaved).isFalse()
        assertThat(promotions.page).isEqualTo(page)
        assertThat(promotions.pageSize).isEqualTo(pageSize)
    }

    @Test
    fun `진행 상태에 대한 필터링 조건이 주어지고, 프로모션 리스트를 조회하면, 진행 상태가 UPCOMING인 프로모션만 조회된다`() {
        // given
        promotionCoreRepository.create(
            createPromotion(
                promotionType = PromotionType.FREE,
                startedAt = LocalDate.now().plusDays(1),
                endedAt = LocalDate.now().plusDays(5),
            ),
        )
        promotionCoreRepository.create(
            createPromotion(
                promotionType = PromotionType.DISCOUNT,
                startedAt = LocalDate.now().minusDays(5),
                endedAt = LocalDate.now().minusDays(1),
            ),
        )

        val progressStatus = PromotionProgressStatus.UPCOMING
        val page = 0
        val pageSize = 10

        // when
        val promotions = promotionCoreRepository.findPromotionList(
            requestUserId = null,
            type = null,
            progressStatus = progressStatus,
            regions = emptySet(),
            photographyTypes = emptySet(),
            sortType = PromotionSortType.LATEST,
            page = page,
            pageSize = pageSize,
        )

        // then
        assertThat(promotions.items).hasSize(1)
        assertThat(promotions.items.first().startedAt).isAfter(LocalDate.now())
    }

    @Test
    fun `진행 상태에 대한 필터링 조건이 주어지고, 프로모션 리스트를 조회하면, 진행 상태가 ENDED인 프로모션만 조회된다`() {
        // given
        promotionCoreRepository.create(
            createPromotion(
                promotionType = PromotionType.FREE,
                startedAt = LocalDate.now().minusDays(10),
                endedAt = LocalDate.now().minusDays(5),
            ),
        )
        promotionCoreRepository.create(
            createPromotion(
                promotionType = PromotionType.DISCOUNT,
                startedAt = LocalDate.now().minusDays(2),
                endedAt = LocalDate.now().plusDays(2),
            ),
        )

        val progressStatus = PromotionProgressStatus.ENDED
        val page = 0
        val pageSize = 10

        // when
        val promotions = promotionCoreRepository.findPromotionList(
            requestUserId = null,
            type = null,
            progressStatus = progressStatus,
            regions = emptySet(),
            photographyTypes = emptySet(),
            sortType = PromotionSortType.LATEST,
            page = page,
            pageSize = pageSize,
        )

        // then
        assertThat(promotions.items).hasSize(1)
        assertThat(promotions.items.first().endedAt).isBefore(LocalDate.now())
    }

    @Test
    fun `지역에 대한 필터링 조건이 주어지고, 프로모션 리스트를 조회하면, 특정 지역에 해당하는 프로모션만 조회된다`() {
        // given
        promotionCoreRepository.create(
            createPromotion(
                promotionType = PromotionType.FREE,
                activeRegions = setOf(createRegion(category = "RegionA", name = "CityA")),
            ),
        )
        promotionCoreRepository.create(
            createPromotion(
                promotionType = PromotionType.DISCOUNT,
                activeRegions = setOf(createRegion(category = "RegionB", name = "CityB")),
            ),
        )

        val regions = setOf(RegionFilterCondition(category = "RegionA", name = "CityA"))
        val page = 0
        val pageSize = 10

        // when
        val promotions = promotionCoreRepository.findPromotionList(
            requestUserId = null,
            type = null,
            progressStatus = null,
            regions = regions,
            photographyTypes = emptySet(),
            sortType = PromotionSortType.LATEST,
            page = page,
            pageSize = pageSize,
        )

        // then
        assertThat(promotions.items).hasSize(1)
        assertThat(promotions.items.first().activeRegions.map { it.category }).contains("RegionA")
    }

    @Test
    fun `저장된 프로모션 리스트를 조회한다`() {
        // given
        val requestUser = userCoreRepository.create(createUser())
        val promotion1 = promotionCoreRepository.create(createPromotion(authorId = requestUser.id))
        val promotion2 = promotionCoreRepository.create(createPromotion(authorId = null))
        val promotion3 = promotionCoreRepository.create(createPromotion(authorId = null))
        promotionSaveCoreRepository.create(PromotionSave.create(userId = requestUser.id, promotionId = promotion1.id))
        promotionSaveCoreRepository.create(PromotionSave.create(userId = requestUser.id, promotionId = promotion2.id))
        promotionSaveCoreRepository.create(PromotionSave.create(userId = requestUser.id, promotionId = promotion3.id))
        val page = 0
        val pageSize = 10

        // when
        val promotions = promotionCoreRepository.findSavedPromotionList(
            requestUserId = requestUser.id,
            page = page,
            pageSize = pageSize,
        )

        // then
        assertThat(promotions.items).hasSize(3)
        assertThat(promotions.items.first().saveCount).isEqualTo(1)
        assertThat(promotions.items.first().isSaved).isTrue()
        assertThat(promotions.page).isEqualTo(page)
        assertThat(promotions.pageSize).isEqualTo(pageSize)
    }

    @Test
    fun `신규 프로모션을 저장한다`() {
        // given
        val promotion = createPromotion()

        // when
        val createdPromotion = promotionCoreRepository.create(promotion)

        // then
        assertThat(createdPromotion.id).isGreaterThan(0)
        assertThat(createdPromotion.promotionType).isEqualTo(promotion.promotionType)
        assertThat(createdPromotion.title).isEqualTo(promotion.title)
        assertThat(createdPromotion.content).isEqualTo(promotion.content)
        assertThatIterable(createdPromotion.images).isEqualTo(promotion.images)
        assertThatIterable(createdPromotion.activeRegions).isEqualTo(promotion.activeRegions)
        assertThatIterable(createdPromotion.hashtags).isEqualTo(promotion.hashtags)
    }

    @Test
    fun `수정된 프로모션 정보가 주어지고, 프로모션을 수정한다`() {
        // given
        val promotion = promotionCoreRepository.create(createPromotion())
        val newPromotion = createPromotion(id = promotion.id)

        // when
        val updatedPromotion = promotionCoreRepository.update(newPromotion)

        // then
        assertThat(updatedPromotion.id).isEqualTo(newPromotion.id)
        assertThat(updatedPromotion.authorId).isEqualTo(newPromotion.authorId)
        assertThat(updatedPromotion.promotionType).isEqualTo(newPromotion.promotionType)
        assertThat(updatedPromotion.title).isEqualTo(newPromotion.title)
        assertThat(updatedPromotion.content).isEqualTo(newPromotion.content)
        assertThat(updatedPromotion.externalLink).isEqualTo(newPromotion.externalLink)
        assertThat(updatedPromotion.startedAt).isEqualTo(newPromotion.startedAt)
        assertThat(updatedPromotion.endedAt).isEqualTo(newPromotion.endedAt)
        assertThat(updatedPromotion.viewCount).isEqualTo(newPromotion.viewCount)
    }

    @Test
    fun `프로모션을 삭제한다`() {
        // given
        val promotion = promotionCoreRepository.create(createPromotion())

        // when
        promotionCoreRepository.delete(promotion)

        // then
        val optionalPromotionJpaEntity = promotionJpaRepository.findById(promotion.id)
        assertThat(optionalPromotionJpaEntity.isEmpty).isTrue()
    }
}
