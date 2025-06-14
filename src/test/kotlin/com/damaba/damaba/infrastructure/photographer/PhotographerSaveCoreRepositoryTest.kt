package com.damaba.damaba.infrastructure.photographer

import com.damaba.damaba.config.RepositoryTestConfig
import com.damaba.damaba.domain.photographer.PhotographerSave
import com.damaba.damaba.util.RandomTestUtils.Companion.randomLong
import org.assertj.core.api.Assertions.assertThat
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import kotlin.test.Test

@ActiveProfiles("test")
@Import(RepositoryTestConfig::class, PhotographerSaveCoreRepository::class)
@DataJpaTest
class PhotographerSaveCoreRepositoryTest @Autowired constructor(
    private val sut: PhotographerSaveCoreRepository,
    private val photographerSaveJpaRepository: PhotographerSaveJpaRepository,
) {
    @Test
    fun `(Find) userId와 photographerId가 주어지고, 사진작가 저장 이력을 조회한다`() {
        // given
        val userId = randomLong()
        val photographerId = randomLong()
        sut.create(PhotographerSave.create(userId, photographerId))

        // when
        val result = sut.findByUserIdAndPhotographerId(userId, photographerId)

        // then
        assertThat(result).isNotNull()
        assertThat(result!!.id).isGreaterThan(0L)
        assertThat(result.userId).isEqualTo(userId)
        assertThat(result.photographerId).isEqualTo(photographerId)
    }

    @Test
    fun `(Find) userId와 photographerId가 주어지고, 사진작가 저장 이력을 조회한다, 존재하지 않는다면 null이 반환된다`() {
        // given
        val userId = randomLong()
        val photographerId = randomLong()

        // when
        val result = sut.findByUserIdAndPhotographerId(userId, photographerId)

        // then
        assertThat(result).isNull()
    }

    @Test
    fun `사진작가 저장 이력 존재 여부를 조회한다, 만약 존재한다면 true가 반환된다`() {
        // given
        val userId = randomLong()
        val photographerId = randomLong()
        sut.create(PhotographerSave.create(userId, photographerId))

        // when
        val result = sut.existsByUserIdAndPhotographerId(userId, photographerId)

        // then
        assertThat(result).isTrue()
    }

    @Test
    fun `사진작가 저장 이력 존재 여부를 조회한다, 만약 존재하지 않는다면 false가 반환된다`() {
        // when
        val result = sut.existsByUserIdAndPhotographerId(randomLong(), randomLong())

        // then
        assertThat(result).isFalse()
    }

    @Test
    fun `사진작가 저장 이력을 생성 및 저장한다`() {
        // given
        val userId = randomLong()
        val photographerId = randomLong()

        // when
        sut.create(PhotographerSave.create(userId, photographerId))

        // then
        val result = photographerSaveJpaRepository.findAll()
        assertThat(result.size).isEqualTo(1)
    }

    @Test
    fun `사진작가 저장 이력을 삭제한다`() {
        // given
        val userId = randomLong()
        val photographerId = randomLong()
        sut.create(PhotographerSave.create(userId, photographerId))
        val photographerSave = sut.findByUserIdAndPhotographerId(userId, photographerId)

        // when
        sut.delete(photographerSave!!)

        // then
        val photographerSaves = photographerSaveJpaRepository.findAll()
        assertThat(photographerSaves.size).isEqualTo(0)
    }
}
