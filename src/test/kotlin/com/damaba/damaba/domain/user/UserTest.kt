package com.damaba.damaba.domain.user

import com.damaba.damaba.domain.file.Image
import com.damaba.damaba.domain.user.constant.Gender
import com.damaba.damaba.domain.user.constant.LoginType
import com.damaba.damaba.domain.user.constant.UserRoleType
import com.damaba.damaba.domain.user.constant.UserType
import com.damaba.damaba.util.RandomTestUtils.Companion.randomString
import com.damaba.damaba.util.fixture.FileFixture.createImage
import com.damaba.damaba.util.fixture.UserFixture.createUser
import com.damaba.damaba.util.fixture.UserFixture.createUserProfile
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
        assertThat(userCreated.type).isEqualTo(UserType.UNDEFINED)
        assertThat(userCreated.roles).containsOnly(UserRoleType.USER)
        assertThat(userCreated.loginType).isEqualTo(loginType)
        assertThat(userCreated.oAuthLoginUid).isEqualTo(oAuthLoginUid)
        assertThat(userCreated.nickname).isEqualTo(nickname)
        assertThat(userCreated.profileImage).isNull()
        assertThat(userCreated.instagramId).isNull()

        // 유저가 생성된 직후에는 isRegistrationCompleted가 false
        assertThat(userCreated.isRegistrationCompleted).isFalse()
    }

    @Test
    fun `User setter test`() {
        // given
        val user = object : User(
            id = 0L,
            loginType = LoginType.KAKAO,
            oAuthLoginUid = "uid",
            type = UserType.UNDEFINED,
            roles = setOf(UserRoleType.USER),
            nickname = "initialNickname",
            profileImage = null,
            gender = Gender.MALE,
            instagramId = null,
        ) {
            fun updateType(type: UserType) {
                this.type = type
            }

            fun updateRoles(roles: Set<UserRoleType>) {
                this.roles = roles
            }

            fun updateNickname(newNickname: String) {
                this.nickname = newNickname
            }

            fun updateProfileImage(profileImage: Image) {
                this.profileImage = profileImage
            }

            fun updateGender(gender: Gender) {
                this.gender = gender
            }

            fun updateInstagramId(instagramId: String?) {
                this.instagramId = instagramId
            }
        }

        val newType = UserType.PHOTOGRAPHER
        val newRoles = setOf(UserRoleType.USER, UserRoleType.ADMIN)
        val newNickname = randomString(len = 5)
        val newProfileImage = createImage()
        val newGender = Gender.FEMALE
        val newInstagramId = randomString(len = 15)

        // when
        user.updateType(newType)
        user.updateRoles(newRoles)
        user.updateNickname(newNickname)
        user.updateProfileImage(newProfileImage)
        user.updateGender(newGender)
        user.updateInstagramId(newInstagramId)

        // then
        assertThat(user.type).isEqualTo(newType)
        assertThat(user.roles).isEqualTo(newRoles)
        assertThat(user.nickname).isEqualTo(newNickname)
        assertThat(user.profileImage).isEqualTo(newProfileImage)
        assertThat(user.gender).isEqualTo(newGender)
        assertThat(user.instagramId).isEqualTo(newInstagramId)
    }

    @Test
    fun `등록되지 않은 유저를 일반 유저로 등록한다`() {
        // given
        val nickname = randomString()
        val gender = Gender.FEMALE
        val instagramId = randomString()
        val user = createUser(type = UserType.UNDEFINED)

        // when
        user.registerUser(nickname, gender, instagramId)

        // then
        assertThat(user.nickname).isEqualTo(nickname)
        assertThat(user.gender).isEqualTo(gender)
        assertThat(user.instagramId).isEqualTo(instagramId)
    }

    @Test
    fun `유저 정보를 업데이트한다`() {
        // given
        val user = createUser()
        val newProfile = createUserProfile()

        // when
        user.updateProfile(newProfile)

        // then
        assertThat(user.nickname).isEqualTo(newProfile.nickname)
        assertThat(user.instagramId).isEqualTo(newProfile.instagramId)
        assertThat(user.profileImage).isEqualTo(newProfile.profileImage)
    }

    @Test
    fun `id가 동일한 user는 같은 객체이다`() {
        // given
        val user1 = createUser(id = 1L)
        val user2 = createUser(id = 1L)

        // when
        val result = user1 == user2

        // then
        assertThat(result).isTrue()
    }

    @Test
    fun `id가 다른 user는 다른 객체이다`() {
        // given
        val user1 = createUser(id = 1L)
        val user2 = createUser(id = 2L)

        // when
        val result = user1 == user2

        // then
        assertThat(result).isFalse()
    }
}
