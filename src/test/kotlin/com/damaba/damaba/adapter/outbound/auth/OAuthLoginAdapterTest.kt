package com.damaba.damaba.adapter.outbound.auth

import com.damaba.damaba.adapter.outbound.kakao.KakaoKApiFeignClient
import com.damaba.damaba.adapter.outbound.kakao.KakaoUserInfoResponse
import com.damaba.damaba.util.RandomTestUtils.Companion.randomString
import com.damaba.user.domain.user.constant.LoginType
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import kotlin.test.Test

class OAuthLoginAdapterTest {
    private val kakaoKApiClient: KakaoKApiFeignClient = mockk()
    private val sut: OAuthLoginAdapter = OAuthLoginAdapter(kakaoKApiClient)

    @Test
    fun `Kakao access token이 주어지고, 주어진 token으로 kakao user id를 조회한다`() {
        // given
        val kakaoAccessToken = randomString()
        val expectedResult = randomString()
        every {
            kakaoKApiClient.getUserInfo(authorizationHeader = "Bearer $kakaoAccessToken")
        } returns KakaoUserInfoResponse(id = expectedResult)

        // when
        val actualResult = sut.getOAuthLoginUid(platform = LoginType.KAKAO, authKey = kakaoAccessToken)

        // then
        verify { kakaoKApiClient.getUserInfo("Bearer $kakaoAccessToken") }
        assertThat(actualResult).isEqualTo(expectedResult)
    }

    @Test
    fun `지원되지 않는 LoginType이 주어지면 IllegalArgumentException이 발생한다`() {
        // given
        val unsupportedPlatform = mockk<LoginType>(relaxed = true) // 강제로 지원하지 않는 LoginType을 생성

        // when
        val ex = catchThrowable {
            assertThat(sut.getOAuthLoginUid(platform = unsupportedPlatform, authKey = "dummy"))
        }

        // then
        assertThat(ex).isInstanceOf(IllegalArgumentException::class.java)
    }
}
