package com.damaba.user.domain.user

import com.damaba.user.domain.user.constant.Gender
import com.damaba.user.domain.user.constant.LoginType
import com.damaba.user.domain.user.constant.UserRoleType
import com.damaba.user.util.RandomTestUtils.Companion.randomInt
import com.damaba.user.util.RandomTestUtils.Companion.randomString
import com.damaba.user.util.TestFixture.createUser
import org.assertj.core.api.Assertions.assertThat
import kotlin.test.Test

class UserDomainEntityTest {
    @Test
    fun `신규 유저를 생성한다`() {
        // given
        val loginType = LoginType.KAKAO
        val oAuthLoginUid = randomString()

        // when
        val userCreated = User.create(loginType, oAuthLoginUid)

        // then
        assertThat(userCreated.id).isEqualTo(0L)
        assertThat(userCreated.roles).containsOnly(UserRoleType.USER)
        assertThat(userCreated.loginType).isEqualTo(loginType)
        assertThat(userCreated.oAuthLoginUid).isEqualTo(oAuthLoginUid)
        assertThat(userCreated.nickname).isNotBlank()
        assertThat(userCreated.profileImageUrl).isEqualTo(User.DEFAULT_PROFILE_IMAGE_URL)
        assertThat(userCreated.gender).isEqualTo(Gender.PRIVATE)
        assertThat(userCreated.age).isEqualTo(-1)
        assertThat(userCreated.instagramId).isNull()

        // 유저가 생성된 직후에는 isRegistrationCompleted가 false
        assertThat(userCreated.isRegistrationCompleted).isFalse()
    }

    @Test
    fun `유저 정보를 업데이트한다`() {
        // given
        val originalUser = createUser()
        val newNickname = randomString()
        val newGender = Gender.FEMALE
        val newAge = randomInt()
        val newInstagramId = randomString()

        // when
        val updatedUser = originalUser.update(
            nickname = newNickname,
            gender = newGender,
            age = newAge,
            instagramId = newInstagramId,
        )

        // then
        assertThat(updatedUser.nickname).isEqualTo(newNickname)
        assertThat(updatedUser.gender).isEqualTo(newGender)
        assertThat(updatedUser.age).isEqualTo(newAge)
        assertThat(updatedUser.instagramId).isEqualTo(newInstagramId)

        assertThat(updatedUser.id).isEqualTo(originalUser.id)
        assertThat(updatedUser.roles).isEqualTo(originalUser.roles)
        assertThat(updatedUser.loginType).isEqualTo(originalUser.loginType)
        assertThat(updatedUser.oAuthLoginUid).isEqualTo(originalUser.oAuthLoginUid)
        assertThat(updatedUser.profileImageUrl).isEqualTo(originalUser.profileImageUrl)
    }

    @Test
    fun `유저 정보를 업데이트 할 때 수정할 정보가 null이라면, 정보는 변경되지 않는다`() {
        // given
        val originalUser = createUser()

        // when
        val updatedUser = originalUser.update(
            nickname = null,
            gender = null,
            age = null,
            instagramId = null,
        )

        // then
        assertThat(updatedUser).isEqualTo(originalUser)
    }
}
