package com.damaba.damaba.domain.promotion.exception

import org.assertj.core.api.Assertions.assertThat
import kotlin.test.Test

class PromotionAuthorHiddenPermissionDeniedExceptionTest {
    @Test
    fun `PromotionAuthorHiddenPermissionDeniedException 생성 시 올바른 속성값을 가진다`() {
        // given & when
        val exception = PromotionAuthorHiddenPermissionDeniedException()

        // then
        assertThat(exception.httpStatusCode).isEqualTo(403)
        assertThat(exception.code).isEqualTo("PROMOTION_AUTHOR_HIDDEN_PERMISSION_DENIED")
        assertThat(exception.message).isEqualTo("작성자 정보 숨김은 관리자만 설정할 수 있습니다.")
    }
}
