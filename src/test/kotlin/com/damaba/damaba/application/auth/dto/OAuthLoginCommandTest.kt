package com.damaba.damaba.application.auth.dto

import com.damaba.damaba.domain.exception.ValidationException
import com.damaba.damaba.domain.user.constant.LoginType
import org.assertj.core.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class OAuthLoginCommandTest {
    @ValueSource(strings = ["", "   "])
    @ParameterizedTest
    fun `auth key가 공백으로 주어지면, validation exception이 발생한다`(emptyString: String) {
        // when
        val ex = Assertions.catchThrowable { OAuthLoginCommand(loginType = LoginType.KAKAO, authKey = emptyString) }

        // then
        Assertions.assertThat(ex).isNotNull()
        Assertions.assertThat(ex).isInstanceOf(ValidationException::class.java)
    }
}
