package com.damaba.damaba.util.fixture

import com.damaba.damaba.domain.common.Address
import com.damaba.damaba.util.RandomTestUtils.Companion.randomString

object AddressFixture {
    fun createAddress(
        sido: String = randomString(),
        sigungu: String = randomString(),
        roadAddress: String = randomString(),
        jibunAddress: String = randomString(),
    ) = Address(sido, sigungu, roadAddress, jibunAddress)
}
