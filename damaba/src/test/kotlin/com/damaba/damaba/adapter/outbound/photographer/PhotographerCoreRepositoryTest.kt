package com.damaba.damaba.adapter.outbound.photographer

import com.damaba.damaba.config.JpaConfig
import com.damaba.damaba.domain.photographer.Photographer
import com.damaba.damaba.domain.photographer.exception.PhotographerNotFoundException
import com.damaba.damaba.util.TestFixture.createPhotographer
import com.damaba.damaba.util.TestFixture.createPhotographerJpaEntity
import com.damaba.damaba.util.TestFixture.createUserJpaEntity
import com.damaba.user.adapter.outbound.user.UserJpaEntity
import com.damaba.user.adapter.outbound.user.UserJpaRepository
import com.damaba.user.adapter.outbound.user.UserProfileImageJpaEntity
import com.damaba.user.adapter.outbound.user.UserProfileImageJpaRepository
import com.damaba.user.domain.user.constant.UserType
import com.damaba.user.domain.user.exception.UserNotFoundException
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import kotlin.test.Test

@ActiveProfiles("test")
@Import(JpaConfig::class, PhotographerCoreRepository::class)
@DataJpaTest
class PhotographerCoreRepositoryTest @Autowired constructor(
    private val entityManager: EntityManager,
    private val photographerCoreRepository: PhotographerCoreRepository,
    private val photographerJpaRepository: PhotographerJpaRepository,
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
        assertThat(ex).isInstanceOf(PhotographerNotFoundException::class.java)
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
    fun `기존 유저 데이터가 존재할 때, 사진작가를 저장한다`() {
        // given
        val savedUserJpaEntity = userJpaRepository.save(createUserJpaEntity())
        val userId = savedUserJpaEntity.id
        val photographer = createPhotographer(id = userId, nickname = "newNick")

        // when
        val result = photographerCoreRepository.saveIfUserExists(photographer)

        // then
        assertEquals(result, photographer)

        val optionalUpdatedUser = userJpaRepository.findById(userId)
        assertThat(optionalUpdatedUser).isPresent()
        val updatedUser = optionalUpdatedUser.get()
        assertEquals(photographer, updatedUser)

        val savedPhotographer = photographerCoreRepository.getById(userId)
        assertEquals(savedPhotographer, photographer)

        val savedProfileImage = userProfileImageJpaRepository.findByUrl(photographer.profileImage.url)
        assertThat(savedProfileImage).isNotNull
    }

    @Test
    fun `기존 유저와 프로필 이미지 데이터가 존재할 때, 사진작가를 저장하면, 기존 프로필 이미지가 삭제되고 사진작가로 저장된다`() {
        // given
        val savedUserJpaEntity = userJpaRepository.save(createUserJpaEntity())
        val userId = savedUserJpaEntity.id
        val originalProfileImage = userProfileImageJpaRepository.save(
            UserProfileImageJpaEntity(
                userId = userId,
                name = savedUserJpaEntity.profileImage.name,
                url = savedUserJpaEntity.profileImage.url,
            ),
        )
        val photographer = createPhotographer(id = userId, nickname = "newNick")

        // when
        val result = photographerCoreRepository.saveIfUserExists(photographer)

        // then
        assertEquals(result, photographer)

        val optionalUpdatedUser = userJpaRepository.findById(userId)
        assertThat(optionalUpdatedUser).isPresent()
        val updatedUser = optionalUpdatedUser.get()
        assertEquals(photographer, updatedUser)

        val savedPhotographer = photographerCoreRepository.getById(userId)
        assertEquals(savedPhotographer, photographer)

        assertThat(userProfileImageJpaRepository.findByUrl(originalProfileImage.url)).isNull()
        val newProfileImage = userProfileImageJpaRepository.findByUrl(photographer.profileImage.url)
        assertThat(newProfileImage).isNotNull
    }

    @Test
    fun `기존 유저 데이터가 존재하지 않을 때, 사진작가를 저장하면, 예외가 발생한다`() {
        // given
        val photographer = createPhotographer(nickname = "newNick")

        // when
        val ex = catchThrowable { photographerCoreRepository.saveIfUserExists(photographer) }

        // then
        assertThat(ex).isInstanceOf(UserNotFoundException::class.java)
    }

    private fun assertEquals(photographer: Photographer, userJpaEntity: UserJpaEntity) {
        assertThat(userJpaEntity.type).isEqualTo(UserType.PHOTOGRAPHER)
        assertThat(userJpaEntity.nickname).isEqualTo(photographer.nickname)
        assertThat(userJpaEntity.profileImage.name).isEqualTo(photographer.profileImage.name)
        assertThat(userJpaEntity.profileImage.url).isEqualTo(photographer.profileImage.url)
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
        assertThat(p1.businessSchedule).isEqualTo(p2.businessSchedule)
        assertThat(p1.portfolio).isEqualTo(p2.portfolio)
        assertThat(p1.activeRegions).isEqualTo(p2.activeRegions)
    }
}
