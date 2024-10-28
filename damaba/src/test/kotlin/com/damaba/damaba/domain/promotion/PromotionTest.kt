package com.damaba.damaba.domain.promotion

import com.damaba.damaba.util.TestFixture.createPromotion
import org.assertj.core.api.Assertions.assertThat
import kotlin.test.Test

class PromotionTest {
    @Test
    fun `Id가 같은 promotion은 동일한 promotion으로 간주한다`() {
        // given
        val promotion1 = createPromotion(id = 1L)
        val promotion2 = createPromotion(id = 1L)

        // when
        val result = promotion1 == promotion2

        // then
        assertThat(result).isTrue()
    }

    @Test
    fun `Id가 다른 promotion은 다른 promotion으로 간주한다`() {
        // given
        val promotion1 = createPromotion(id = 1L)
        val promotion2 = createPromotion(id = 2L)

        // when
        val result = promotion1 == promotion2

        // then
        assertThat(result).isFalse()
    }
}
