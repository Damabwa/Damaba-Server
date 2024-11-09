package com.damaba.user.domain.user

import com.damaba.user.domain.user.constant.Gender
import com.damaba.user.domain.user.constant.LoginType
import com.damaba.user.domain.user.constant.UserRoleType
import com.damaba.user.util.RandomTestUtils.Companion.randomString
import com.damaba.user.util.TestFixture.createUser
import org.assertj.core.api.Assertions.assertThat
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
        assertThat(userCreated.gender).isEqualTo(User.DEFAULT_GENDER)
        assertThat(userCreated.instagramId).isNull()

        // 유저가 생성된 직후에는 isRegistrationCompleted가 false
        assertThat(userCreated.isRegistrationCompleted).isFalse()
    }

    @Test
    fun `유저 정보를 업데이트한다`() {
        // given
        val user = createUser()
        val newNickname = randomString()
        val newGender = Gender.FEMALE
        val newInstagramId = randomString()
        val newProfileImageUrl = randomString()

        // when
        user.update(
            nickname = newNickname,
            gender = newGender,
            instagramId = newInstagramId,
            profileImageUrl = newProfileImageUrl,
        )

        // then
        assertThat(user.nickname).isEqualTo(newNickname)
        assertThat(user.gender).isEqualTo(newGender)
        assertThat(user.instagramId).isEqualTo(newInstagramId)
        assertThat(user.profileImageUrl).isEqualTo(newProfileImageUrl)
    }
}
