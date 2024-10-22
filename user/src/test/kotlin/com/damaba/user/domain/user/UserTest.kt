package com.damaba.user.domain.user

import com.damaba.user.domain.user.constant.Gender
import com.damaba.user.domain.user.constant.LoginType
import com.damaba.user.domain.user.constant.UserRoleType
import com.damaba.user.util.RandomTestUtils.Companion.randomLocalDate
import com.damaba.user.util.RandomTestUtils.Companion.randomString
import com.damaba.user.util.TestFixture.createUser
import org.assertj.core.api.Assertions.assertThat
import java.time.LocalDate
import kotlin.test.Test

class UserTest {
    @Test
    fun `신규 유저를 생성한다`() {
        // given
        val loginType = LoginType.KAKAO
        val oAuthLoginUid = randomString()
        val nickname = randomString(len = 7)

        // when
        val userCreated = User.create(loginType, oAuthLoginUid, nickname)

        // then
        assertThat(userCreated.id).isEqualTo(0L)
        assertThat(userCreated.roles).containsOnly(UserRoleType.USER)
        assertThat(userCreated.loginType).isEqualTo(loginType)
        assertThat(userCreated.oAuthLoginUid).isEqualTo(oAuthLoginUid)
        assertThat(userCreated.nickname).isEqualTo(nickname)
        assertThat(userCreated.profileImageUrl).isEqualTo(User.DEFAULT_PROFILE_IMAGE_URL)
        assertThat(userCreated.gender).isEqualTo(Gender.PRIVATE)
        assertThat(userCreated.birthDate).isEqualTo(LocalDate.MIN)
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
        val newBirthDate = randomLocalDate()
        val newInstagramId = randomString()
        val newProfileImageUrl = randomString()

        // when
        val updatedUser = originalUser.update(
            nickname = newNickname,
            gender = newGender,
            birthDate = newBirthDate,
            instagramId = newInstagramId,
            profileImageUrl = newProfileImageUrl,
        )

        // then
        assertThat(updatedUser.nickname).isEqualTo(newNickname)
        assertThat(updatedUser.gender).isEqualTo(newGender)
        assertThat(updatedUser.birthDate).isEqualTo(newBirthDate)
        assertThat(updatedUser.instagramId).isEqualTo(newInstagramId)
        assertThat(updatedUser.profileImageUrl).isEqualTo(newProfileImageUrl)

        assertThat(updatedUser.id).isEqualTo(originalUser.id)
        assertThat(updatedUser.roles).isEqualTo(originalUser.roles)
        assertThat(updatedUser.loginType).isEqualTo(originalUser.loginType)
        assertThat(updatedUser.oAuthLoginUid).isEqualTo(originalUser.oAuthLoginUid)
    }

    @Test
    fun `유저 정보를 업데이트 할 때 수정할 정보가 null이라면, 정보는 변경되지 않는다`() {
        // given
        val originalUser = createUser()

        // when
        val updatedUser = originalUser.update(
            nickname = null,
            gender = null,
            birthDate = null,
            instagramId = null,
            profileImageUrl = null,
        )

        // then
        assertThat(updatedUser).isEqualTo(originalUser)
    }
}
