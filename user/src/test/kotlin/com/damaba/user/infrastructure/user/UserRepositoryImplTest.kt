package com.damaba.user.infrastructure.user

import com.damaba.user.domain.user.User
import com.damaba.user.domain.user.constant.LoginType
import com.damaba.user.util.RandomTestUtils.Companion.randomLong
import com.damaba.user.util.RandomTestUtils.Companion.randomString
import org.assertj.core.api.Assertions.assertThat
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import kotlin.test.Test

@ActiveProfiles("test")
@Import(UserRepositoryImpl::class)
@DataJpaTest
class UserRepositoryImplTest @Autowired constructor(
    private val userRepository: UserRepositoryImpl,
) {
    @Test
    fun `신규 유저를 저장한다`() {
        // given
        val user = createUser()

        // when
        val savedUser = userRepository.save(user)

        // then
        val foundUser = userRepository.findById(savedUser.id)
        assertThat(savedUser).isNotNull()
        assertThat(foundUser).isNotNull()
        assertThat(savedUser).isEqualTo(foundUser)
    }

    @Test
    fun `특정 유저의 id가 주어지고, 주어진 id에 해당하는 유저를 조회하면, 유저 정보가 반환된다`() {
        // given
        val savedUser = userRepository.save(createUser())

        // when
        val result = userRepository.findById(savedUser.id)

        // then
        assertThat(result).isNotNull()
        assertThat(result).isEqualTo(savedUser)
    }

    @Test
    fun `존재하지 않는 유저의 id가 주어지고, id로 유저를 조회하면, null이 반환된다`() {
        // given

        // when
        val result = userRepository.findById(randomLong())

        // then
        assertThat(result).isNull()
    }

    @Test
    fun `특정 유저의 OAuth login user id가 주어지고, 주어진 uid에 해당하는 유저를 조회하면, 유저 정보가 반환된다`() {
        // given
        val oAuthLoginUid = randomString()
        userRepository.save(createUser(oAuthLoginUid = oAuthLoginUid))

        // when
        val result = userRepository.findByOAuthLoginUid(oAuthLoginUid)

        // then
        assertThat(result).isNotNull()
        assertThat(result?.id).isGreaterThan(0)
        assertThat(result?.oAuthLoginUid).isEqualTo(oAuthLoginUid)
    }

    @Test
    fun `존재하지 않는 유저의 OAuth login user id가 주어지고, uid로 유저를 조회하면, null이 반환된다`() {
        // given

        // when
        val result = userRepository.findByOAuthLoginUid(randomString())

        // then
        assertThat(result).isNull()
    }

    private fun createUser(
        id: Long = randomLong(),
        oAuthLoginUid: String = randomString(),
        loginType: LoginType = LoginType.KAKAO,
    ): User = User(
        id = id,
        oAuthLoginUid = oAuthLoginUid,
        loginType = loginType,
    )
}
