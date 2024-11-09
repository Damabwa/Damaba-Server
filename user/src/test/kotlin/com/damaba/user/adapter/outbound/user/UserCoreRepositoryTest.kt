package com.damaba.user.adapter.outbound.user

import com.damaba.user.domain.user.exception.UserNotFoundException
import com.damaba.user.util.RandomTestUtils.Companion.randomLong
import com.damaba.user.util.RandomTestUtils.Companion.randomString
import com.damaba.user.util.TestFixture.createUser
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import kotlin.test.Test

@ActiveProfiles("test")
@Import(UserCoreRepository::class)
@DataJpaTest
class UserCoreRepositoryTest @Autowired constructor(
    private val userCoreRepository: UserCoreRepository,
    private val userProfileImageJpaRepository: UserProfileImageJpaRepository,
) {

    @Test
    fun `특정 유저의 id가 주어지고, 주어진 id에 해당하는 유저를 조회하면, 유저 정보가 반환된다`() {
        // given
        val savedUser = userCoreRepository.save(createUser())

        // when
        val result = userCoreRepository.findById(savedUser.id)

        // then
        assertThat(result).isNotNull()
        assertThat(result).isEqualTo(savedUser)
    }

    @Test
    fun `존재하지 않는 유저의 id가 주어지고, id로 유저를 조회하면, null이 반환된다`() {
        // given

        // when
        val result = userCoreRepository.findById(randomLong())

        // then
        assertThat(result).isNull()
    }

    @Test
    fun `특정 유저의 OAuth login user id가 주어지고, 주어진 uid에 해당하는 유저를 조회하면, 유저 정보가 반환된다`() {
        // given
        val oAuthLoginUid = randomString()
        userCoreRepository.save(createUser(oAuthLoginUid = oAuthLoginUid))

        // when
        val result = userCoreRepository.findByOAuthLoginUid(oAuthLoginUid)

        // then
        assertThat(result).isNotNull()
        assertThat(result?.id).isGreaterThan(0)
        assertThat(result?.oAuthLoginUid).isEqualTo(oAuthLoginUid)
    }

    @Test
    fun `(Get) 특정 유저의 id가 주어지고, 주어진 id에 해당하는 유저를 조회하면, 유저 정보가 반환된다`() {
        // given
        val savedUser = userCoreRepository.save(createUser())

        // when
        val result = userCoreRepository.getById(savedUser.id)

        // then
        assertThat(result).isNotNull()
        assertThat(result.id).isEqualTo(savedUser.id)
    }

    @Test
    fun `존재하지 않는 유저의 id가 주어지고, 주어진 id에 해당하는 유저를 조회하면, 예외가 발생한다`() {
        // given

        // when
        val ex = catchThrowable { userCoreRepository.getById(id = randomLong()) }

        // then
        assertThat(ex).isInstanceOf(UserNotFoundException::class.java)
    }

    @Test
    fun `닉네임이 주어지고, 주어진 닉네임이 존재하는지 확인한다, 만약 사용중인 닉네임이라면 true가 반환된다`() {
        // given
        val nickname = randomString(len = 7)
        userCoreRepository.save(createUser(nickname = nickname))

        // when
        val exists = userCoreRepository.doesNicknameExist(nickname)

        // then
        assertThat(exists).isTrue()
    }

    @Test
    fun `닉네임이 주어지고, 주어진 닉네임이 존재하는지 확인한다, 만약 사용중인 닉네임이 아니라면 false가 반환된다`() {
        // given
        val nickname = randomString(len = 7)

        // when
        val exists = userCoreRepository.doesNicknameExist(nickname)

        // then
        assertThat(exists).isFalse()
    }

    @Test
    fun `존재하지 않는 유저의 OAuth login user id가 주어지고, uid로 유저를 조회하면, null이 반환된다`() {
        // given

        // when
        val result = userCoreRepository.findByOAuthLoginUid(randomString())

        // then
        assertThat(result).isNull()
    }

    @Test
    fun `신규 유저를 저장한다`() {
        // given
        val user = createUser()

        // when
        val savedUser = userCoreRepository.save(user)

        // then
        val foundUser = userCoreRepository.findById(savedUser.id)
        assertThat(savedUser).isNotNull()
        assertThat(foundUser).isNotNull()
        assertThat(savedUser).isEqualTo(foundUser)
    }

    @Test
    fun `변경되지 않은 기존 유저 정보가 주어지고, 유저를 업데이트하면, 아무런 일도 일어나지 않는다`() {
        // given
        val originalUser = userCoreRepository.save(createUser())

        // when
        val updatedUser = userCoreRepository.update(originalUser)

        // then
        assertThat(updatedUser.id).isEqualTo(originalUser.id)
        assertThat(updatedUser.nickname).isEqualTo(originalUser.nickname)
        assertThat(updatedUser.gender).isEqualTo(originalUser.gender)
        assertThat(updatedUser.instagramId).isEqualTo(originalUser.instagramId)
        assertThat(updatedUser.profileImageUrl).isEqualTo(originalUser.profileImageUrl)
    }

    @Test
    fun `갱신할 유저 정보가 주어지고, 유저를 업데이트하면, 수정된 유저 정보가 반환된다`() {
        // given
        val originalUser =
            userCoreRepository.save(createUser(profileImageUrl = "https://file.test/original-image.jpg"))

        // when
        val result =
            userCoreRepository.update(
                createUser(
                    id = originalUser.id,
                    profileImageUrl = "https://file.test/new-image.jpg",
                ),
            )

        // then
        val updatedUser = userCoreRepository.getById(originalUser.id)
        assertThat(result).isEqualTo(updatedUser)
        assertThat(result.nickname).isEqualTo(updatedUser.nickname)
        assertThat(result.gender).isEqualTo(updatedUser.gender)
        assertThat(result.instagramId).isEqualTo(updatedUser.instagramId)
        assertThat(result.profileImageUrl).isEqualTo(updatedUser.profileImageUrl)
    }

    @Test
    fun `갱신할 유저 정보와 새로운 프로필 이미지가 주어지고, 유저를 업데이트하면, 수정된 유저 정보가 반환되고 기존 프로필 이미지는 삭제된다`() {
        // given
        val originalUser = userCoreRepository.save(
            createUser(profileImageUrl = "https://file.test/original-image.jpg"),
        )
        userProfileImageJpaRepository.save(
            UserProfileImageJpaEntity(
                userId = originalUser.id,
                url = originalUser.profileImageUrl,
                name = "original-image",
            ),
        )

        // when
        val result =
            userCoreRepository.update(
                createUser(
                    id = originalUser.id,
                    profileImageUrl = "https://file.test/new-image.jpg",
                ),
            )

        // then
        val updatedUser = userCoreRepository.getById(originalUser.id)
        assertThat(result).isEqualTo(updatedUser)
        assertThat(result.nickname).isEqualTo(updatedUser.nickname)
        assertThat(result.gender).isEqualTo(updatedUser.gender)
        assertThat(result.instagramId).isEqualTo(updatedUser.instagramId)
        assertThat(result.profileImageUrl).isEqualTo(updatedUser.profileImageUrl)

        val foundOriginalProfileImage = userProfileImageJpaRepository.findByUrl(originalUser.profileImageUrl)
        assertThat(foundOriginalProfileImage).isNull()
    }
}
