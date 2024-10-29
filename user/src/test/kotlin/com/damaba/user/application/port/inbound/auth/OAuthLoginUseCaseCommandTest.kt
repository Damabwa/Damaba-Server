package com.damaba.user.application.port.inbound.auth

import com.damaba.common_exception.ValidationException
import com.damaba.user.domain.user.constant.LoginType
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class OAuthLoginUseCaseCommandTest {
    @ValueSource(strings = ["", "   "])
    @ParameterizedTest
    fun `auth key가 공백으로 주어지면, validation exception이 발생한다`(emptyString: String) {
        // when
        val ex = catchThrowable { OAuthLoginUseCase.Command(loginType = LoginType.KAKAO, authKey = emptyString) }

        // then
        assertThat(ex).isNotNull()
        assertThat(ex).isInstanceOf(ValidationException::class.java)
    }
}
