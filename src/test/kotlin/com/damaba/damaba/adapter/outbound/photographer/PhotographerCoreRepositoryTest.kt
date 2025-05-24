package com.damaba.damaba.adapter.outbound.photographer

import com.damaba.damaba.adapter.outbound.user.UserJpaEntity
import com.damaba.damaba.adapter.outbound.user.UserJpaRepository
import com.damaba.damaba.adapter.outbound.user.UserProfileImageJpaEntity
import com.damaba.damaba.adapter.outbound.user.UserProfileImageJpaRepository
import com.damaba.damaba.config.JpaConfig
import com.damaba.damaba.domain.common.constant.PhotographyType
import com.damaba.damaba.domain.photographer.Photographer
import com.damaba.damaba.domain.photographer.PhotographerSave
import com.damaba.damaba.domain.photographer.constant.PhotographerSortType
import com.damaba.damaba.domain.photographer.exception.PhotographerNotFoundException
import com.damaba.damaba.domain.region.Region
import com.damaba.damaba.domain.region.RegionFilterCondition
import com.damaba.damaba.domain.user.constant.UserType
import com.damaba.damaba.domain.user.exception.UserNotFoundException
import com.damaba.damaba.util.fixture.PhotographerFixture.createPhotographer
import com.damaba.damaba.util.fixture.PhotographerFixture.createPhotographerJpaEntity
import com.damaba.damaba.util.fixture.RegionFixture.createRegion
import com.damaba.damaba.util.fixture.UserFixture.createUserJpaEntity
import com.linecorp.kotlinjdsl.support.spring.data.jpa.autoconfigure.KotlinJdslAutoConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import kotlin.test.Test

@ActiveProfiles("test")
@Import(
    JpaConfig::class,
    KotlinJdslAutoConfiguration::class,
    PhotographerCoreRepository::class,
    PhotographerJdslRepository::class,
    PhotographerSaveCoreRepository::class,
)
@DataJpaTest
class PhotographerCoreRepositoryTest @Autowired constructor(
    private val photographerCoreRepository: PhotographerCoreRepository,
    private val photographerJpaRepository: PhotographerJpaRepository,
    private val photographerSaveCoreRepository: PhotographerSaveCoreRepository,
    private val userJpaRepository: UserJpaRepository,
    private val userProfileImageJpaRepository: UserProfileImageJpaRepository,
) {
    @Test
    fun `(GET) 사진작가를 id로 단건 조회한다`() {
        // given
        val userId = userJpaRepository.save(createUserJpaEntity()).id
        photographerJpaRepository.save(createPhotographerJpaEntity(id = userId))

        // when
        val result = photographerCoreRepository.getById(userId)

        // then
        assertThat(result).isNotNull
    }

    @Test
    fun `(GET) 저장된 유저 데이터가 없을 때, 사진작가를 id로 단건 조회하면, 예외가 발생한다`() {
        // given
        val userId = 1L

        // when
        val ex = catchThrowable { photographerCoreRepository.getById(userId) }

        // then
        assertThat(ex).isInstanceOf(UserNotFoundException::class.java)
    }

    @Test
    fun `(GET) 저장된 사진작가 데이터가 없을 때, 사진작가를 id로 단건 조회하면, 예외가 발생한다`() {
        // given
        val userId = userJpaRepository.save(createUserJpaEntity()).id

        // when
        val ex = catchThrowable { photographerCoreRepository.getById(userId) }

        // then
        assertThat(ex).isInstanceOf(PhotographerNotFoundException::class.java)
    }

    @Test
    fun `필터링 조건이 주어지고, 사진작가 리스트를 조회하면, 주어진 조건에 맞는 사진작가 리스트가 반환된다`() {
        // given
        val user1 = userJpaRepository.save(createUserJpaEntity())
        val reqUserId = user1.id
        val photographer1 = photographerCoreRepository.createIfUserExists(
            createPhotographer(
                id = user1.id,
                mainPhotographyTypes = setOf(PhotographyType.SNAP),
                activeRegions = setOf(createRegion(category = "RegionA", name = "CityA")),
            ),
        )
        photographerSaveCoreRepository.create(
            PhotographerSave.create(userId = reqUserId, photographerId = photographer1.id),
        )

        val user2 = userJpaRepository.save(createUserJpaEntity())
        photographerCoreRepository.createIfUserExists(
            createPhotographer(
                id = user2.id,
                mainPhotographyTypes = setOf(PhotographyType.SNAP),
                activeRegions = setOf(createRegion(category = "RegionA", name = "CityA")),
            ),
        )

        val regions = setOf(
            RegionFilterCondition(category = "RegionA", name = "CityA"),
            RegionFilterCondition(category = "RegionB", name = null),
        )
        val photographyTypes = setOf(PhotographyType.SNAP)
        val sortType = PhotographerSortType.LATEST
        val page = 0
        val pageSize = 10

        // when
        val promotions = photographerCoreRepository.findPhotographerList(
            requestUserId = reqUserId,
            regions = regions,
            photographyTypes = photographyTypes,
            sort = sortType,
            page = page,
            pageSize = pageSize,
        )

        // then
        assertThat(promotions.items).hasSize(2)
        assertThat(promotions.page).isEqualTo(page)
        assertThat(promotions.pageSize).isEqualTo(pageSize)
    }

    @Test
    fun `필터링 조건 없이 사진작가 리스트를 조회하면, 모든 사진작가이 반환된다`() {
        // given
        val user1 = userJpaRepository.save(createUserJpaEntity())
        photographerCoreRepository.createIfUserExists(
            createPhotographer(
                id = user1.id,
                mainPhotographyTypes = setOf(PhotographyType.SNAP),
                activeRegions = setOf(createRegion(category = "RegionA", name = "CityA")),
            ),
        )
        val user2 = userJpaRepository.save(createUserJpaEntity())
        photographerCoreRepository.createIfUserExists(
            createPhotographer(
                id = user2.id,
                mainPhotographyTypes = setOf(PhotographyType.SNAP),
                activeRegions = setOf(createRegion(category = "RegionA", name = "CityA")),
            ),
        )
        val page = 0
        val pageSize = 10

        // when
        val promotions = photographerCoreRepository.findPhotographerList(
            requestUserId = null,
            regions = emptySet(),
            photographyTypes = emptySet(),
            sort = PhotographerSortType.POPULAR,
            page = page,
            pageSize = pageSize,
        )

        // then
        assertThat(promotions.items).hasSize(2)
        assertThat(promotions.items.first().isSaved).isFalse()
        assertThat(promotions.page).isEqualTo(page)
        assertThat(promotions.pageSize).isEqualTo(pageSize)
    }

    @Test
    fun `지역에 대한 필터링 조건이 주어지고, 사진작가 리스트를 조회하면, 특정 지역에 해당하는 사진작가만 조회된다`() {
        // given
        val user1 = userJpaRepository.save(createUserJpaEntity())
        photographerCoreRepository.createIfUserExists(
            createPhotographer(
                id = user1.id,
                mainPhotographyTypes = setOf(PhotographyType.SNAP),
                activeRegions = setOf(createRegion(category = "RegionA", name = "CityA")),
            ),
        )
        val user2 = userJpaRepository.save(createUserJpaEntity())
        photographerCoreRepository.createIfUserExists(
            createPhotographer(
                id = user2.id,
                mainPhotographyTypes = setOf(PhotographyType.SNAP),
                activeRegions = setOf(createRegion(category = "RegionB", name = "CityB")),
            ),
        )
        val regions = setOf(RegionFilterCondition(category = "RegionA", name = "CityA"))
        val page = 0
        val pageSize = 10

        // when
        val photographers = photographerCoreRepository.findPhotographerList(
            requestUserId = null,
            regions = regions,
            photographyTypes = emptySet(),
            sort = PhotographerSortType.LATEST,
            page = page,
            pageSize = pageSize,
        )

        // then
        assertThat(photographers.items).hasSize(1)
    }

    @Test
    fun `저장된 사진작가 리스트를 조회한다`() {
        // given
        val user1 = userJpaRepository.save(createUserJpaEntity())
        val photographer1 = photographerCoreRepository.createIfUserExists(
            createPhotographer(
                id = user1.id,
                mainPhotographyTypes = setOf(PhotographyType.SNAP),
                activeRegions = setOf(createRegion(category = "RegionA", name = "CityA")),
            ),
        )
        photographerSaveCoreRepository.create(
            PhotographerSave.create(
                userId = user1.id,
                photographerId = photographer1.id,
            ),
        )
        val user2 = userJpaRepository.save(createUserJpaEntity())
        val photographer2 = photographerCoreRepository.createIfUserExists(
            createPhotographer(
                id = user2.id,
                mainPhotographyTypes = setOf(PhotographyType.SNAP),
                activeRegions = setOf(createRegion(category = "RegionA", name = "CityA")),
            ),
        )
        photographerSaveCoreRepository.create(
            PhotographerSave.create(
                userId = user1.id,
                photographerId = photographer2.id,
            ),
        )
        val page = 0
        val pageSize = 10

        // when
        val promotions = photographerCoreRepository.findSavedPhotographerList(
            requestUserId = user1.id,
            page = page,
            pageSize = pageSize,
        )

        // then
        assertThat(promotions.items).hasSize(2)
        assertThat(promotions.items.first().isSaved).isTrue()
        assertThat(promotions.page).isEqualTo(page)
        assertThat(promotions.pageSize).isEqualTo(pageSize)
    }

    @Test
    fun `기존 유저 데이터가 존재할 때, 사진작가를 저장한다`() {
        // given
        val createdUserJpaEntity = userJpaRepository.save(createUserJpaEntity(profileImage = null))
        val userId = createdUserJpaEntity.id
        val photographer = createPhotographer(id = userId, nickname = "newNick", profileImage = null)

        // when
        val result = photographerCoreRepository.createIfUserExists(photographer)

        // then
        assertEquals(result, photographer)

        val optionalUpdatedUser = userJpaRepository.findById(userId)
        assertThat(optionalUpdatedUser).isPresent()
        val updatedUser = optionalUpdatedUser.get()
        assertEquals(photographer, updatedUser)

        val createdPhotographer = photographerCoreRepository.getById(userId)
        assertEquals(createdPhotographer, photographer)
    }

    @Test
    fun `기존 유저와 프로필 이미지 데이터가 존재할 때, 사진작가를 저장하면, 기존 프로필 이미지가 삭제되고 사진작가로 저장된다`() {
        // given
        val createdUserJpaEntity = userJpaRepository.save(createUserJpaEntity())
        val userId = createdUserJpaEntity.id
        val originalProfileImage = userProfileImageJpaRepository.save(
            UserProfileImageJpaEntity(
                userId = userId,
                name = createdUserJpaEntity.profileImage!!.name,
                url = createdUserJpaEntity.profileImage!!.url,
            ),
        )
        val photographer = createPhotographer(id = userId, nickname = "newNick")

        // when
        val result = photographerCoreRepository.createIfUserExists(photographer)

        // then
        assertEquals(result, photographer)

        val optionalUpdatedUser = userJpaRepository.findById(userId)
        assertThat(optionalUpdatedUser).isPresent()
        val updatedUser = optionalUpdatedUser.get()
        assertEquals(photographer, updatedUser)

        val createdPhotographer = photographerCoreRepository.getById(userId)
        assertEquals(createdPhotographer, photographer)

        assertThat(userProfileImageJpaRepository.findByUrl(originalProfileImage.url)).isNull()
        val newProfileImage = userProfileImageJpaRepository.findByUrl(photographer.profileImage!!.url)
        assertThat(newProfileImage).isNotNull
    }

    @Test
    fun `기존 유저 데이터가 존재하지 않을 때, 사진작가를 저장하면, 예외가 발생한다`() {
        // given
        val photographer = createPhotographer(nickname = "newNick")

        // when
        val ex = catchThrowable { photographerCoreRepository.createIfUserExists(photographer) }

        // then
        assertThat(ex).isInstanceOf(UserNotFoundException::class.java)
    }

    @Test
    fun `작가 정보가 주어지고, 주어진 작가로 작가 정보를 업데이트하면, 갱신된 작가 정보가 반환된다`() {
        // given
        val originalUserJpaEntity = userJpaRepository.save(createUserJpaEntity())
        val photographerJpaEntity = photographerCoreRepository.createIfUserExists(
            createPhotographer(
                id = originalUserJpaEntity.id,
                mainPhotographyTypes = setOf(PhotographyType.PROFILE),
                activeRegions = setOf(Region("서울", "강남구")),
            ),
        )
        val newPhotographer = createPhotographer(
            id = photographerJpaEntity.id,
            profileImage = originalUserJpaEntity.profileImage?.toImage(),
            mainPhotographyTypes = setOf(PhotographyType.SNAP),
            activeRegions = setOf(Region("대전", "서구")),
        )

        // when
        val updatedPhotographer = photographerCoreRepository.update(newPhotographer)

        // then
        val foundPhotographer = photographerCoreRepository.getById(originalUserJpaEntity.id)
        assertThat(foundPhotographer).isEqualTo(updatedPhotographer) // update가 반환한 작가가 조회된 작가와 같아야 함
    }

    @Test
    fun `(기존 프로필 이미지가 없는 경우) 작가 정보가 주어지고, 주어진 작가로 작가 정보를 업데이트하면, 갱신된 작가 정보가 반환된다`() {
        // given
        val originalUserJpaEntity = userJpaRepository.save(createUserJpaEntity(profileImage = null))
        val photographerJpaEntity = photographerCoreRepository.createIfUserExists(
            createPhotographer(
                id = originalUserJpaEntity.id,
                mainPhotographyTypes = setOf(PhotographyType.PROFILE),
                activeRegions = setOf(Region("서울", "강남구")),
            ),
        )
        val newPhotographer = createPhotographer(
            id = photographerJpaEntity.id,
            profileImage = originalUserJpaEntity.profileImage?.toImage(),
            mainPhotographyTypes = setOf(PhotographyType.SNAP),
            activeRegions = setOf(Region("대전", "서구")),
        )

        // when
        val updatedPhotographer = photographerCoreRepository.update(newPhotographer)

        // then
        val foundPhotographer = photographerCoreRepository.getById(originalUserJpaEntity.id)
        assertThat(foundPhotographer).isEqualTo(updatedPhotographer) // update가 반환한 작가가 조회된 작가와 같아야 함
    }

    @Test
    fun `작가 정보가 주어지고, 주어진 작가로 작가 정보를 업데이트한다, 만약 프로필 이미지가 변경되었다면 기존 이미지를 삭제하고 신규 이미지를 저장한다`() {
        // given
        val userJpaEntity = userJpaRepository.save(createUserJpaEntity())
        val photographerJpaEntity = photographerCoreRepository.createIfUserExists(
            createPhotographer(
                id = userJpaEntity.id,
                mainPhotographyTypes = setOf(PhotographyType.PROFILE),
                activeRegions = setOf(Region("서울", "강남구")),
            ),
        )
        val newPhotographer = createPhotographer(
            id = photographerJpaEntity.id,
            mainPhotographyTypes = setOf(PhotographyType.SNAP),
            activeRegions = setOf(Region("대전", "서구")),
        )

        // when
        val updatedPhotographer = photographerCoreRepository.update(newPhotographer)

        // then
        val foundPhotographer = photographerCoreRepository.getById(userJpaEntity.id)
        assertThat(foundPhotographer).isEqualTo(updatedPhotographer) // update가 반환한 작가가 조회된 작가와 같아야 함
    }

    @Test
    fun `(기존 이미지가 없는 경우) 작가 정보가 주어지고, 주어진 작가로 작가 정보를 업데이트한다, 만약 프로필 이미지가 변경되었다면 기존 이미지를 삭제하고 신규 이미지를 저장한다`() {
        // given
        val originalUserJpaEntity = userJpaRepository.save(createUserJpaEntity(profileImage = null))
        val photographerJpaEntity = photographerCoreRepository.createIfUserExists(
            createPhotographer(
                id = originalUserJpaEntity.id,
                profileImage = null,
                mainPhotographyTypes = setOf(PhotographyType.PROFILE),
                activeRegions = setOf(Region("서울", "강남구")),
            ),
        )
        val newPhotographer = createPhotographer(
            id = photographerJpaEntity.id,
            mainPhotographyTypes = setOf(PhotographyType.SNAP),
            activeRegions = setOf(Region("대전", "서구")),
        )

        // when
        val updatedPhotographer = photographerCoreRepository.update(newPhotographer)

        // then
        val foundPhotographer = photographerCoreRepository.getById(originalUserJpaEntity.id)
        assertThat(foundPhotographer).isEqualTo(updatedPhotographer) // update가 반환한 작가가 조회된 작가와 같아야 함
    }

    @Test
    fun `(이미지를 null로 업데이트하는 경우) 작가 정보가 주어지고, 주어진 작가로 작가 정보를 업데이트한다, 만약 프로필 이미지가 변경되었다면 기존 이미지를 삭제하고 신규 이미지를 저장한다`() {
        // given
        val userJpaEntity = userJpaRepository.save(createUserJpaEntity())
        val photographerJpaEntity = photographerCoreRepository.createIfUserExists(
            createPhotographer(
                id = userJpaEntity.id,
                mainPhotographyTypes = setOf(PhotographyType.PROFILE),
                activeRegions = setOf(Region("서울", "강남구")),
            ),
        )
        val newPhotographer = createPhotographer(
            id = photographerJpaEntity.id,
            profileImage = null,
            mainPhotographyTypes = setOf(PhotographyType.SNAP),
            activeRegions = setOf(Region("대전", "서구")),
        )

        // when
        val updatedPhotographer = photographerCoreRepository.update(newPhotographer)

        // then
        val foundPhotographer = photographerCoreRepository.getById(userJpaEntity.id)
        assertThat(foundPhotographer).isEqualTo(updatedPhotographer) // update가 반환한 작가가 조회된 작가와 같아야 함
    }

    private fun assertEquals(photographer: Photographer, userJpaEntity: UserJpaEntity) {
        assertThat(userJpaEntity.type).isEqualTo(UserType.PHOTOGRAPHER)
        assertThat(userJpaEntity.nickname).isEqualTo(photographer.nickname)
        assertThat(userJpaEntity.profileImage?.name).isEqualTo(photographer.profileImage?.name)
        assertThat(userJpaEntity.profileImage?.url).isEqualTo(photographer.profileImage?.url)
        assertThat(userJpaEntity.gender).isEqualTo(photographer.gender)
        assertThat(userJpaEntity.instagramId).isEqualTo(photographer.instagramId)
    }

    private fun assertEquals(p1: Photographer, p2: Photographer) {
        assertThat(p1.type).isEqualTo(p2.type)
        assertThat(p1.nickname).isEqualTo(p2.nickname)
        assertThat(p1.profileImage).isEqualTo(p2.profileImage)
        assertThat(p1.gender).isEqualTo(p2.gender)
        assertThat(p1.instagramId).isEqualTo(p2.instagramId)
        assertThat(p1.mainPhotographyTypes).isEqualTo(p2.mainPhotographyTypes)
        assertThat(p1.contactLink).isEqualTo(p2.contactLink)
        assertThat(p1.description).isEqualTo(p2.description)
        assertThat(p1.address).isEqualTo(p2.address)
        assertThat(p1.portfolio).isEqualTo(p2.portfolio)
        assertThat(p1.activeRegions).isEqualTo(p2.activeRegions)
    }
}
