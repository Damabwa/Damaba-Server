package com.damaba.damaba.adapter.outbound.promotion

import com.damaba.damaba.domain.common.Address
import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
data class PromotionAddressJpaEmbeddable(
    @Column(name = "sido", nullable = false)
    val sido: String,

    @Column(name = "sigungu", nullable = false)
    val sigungu: String,

    @Column(name = "road_address", nullable = false)
    val roadAddress: String,

    @Column(name = "jibun_address", nullable = false)
    val jibunAddress: String,
) {
    fun toAddress() = Address(
        sido = this.sido,
        sigungu = this.sigungu,
        roadAddress = this.roadAddress,
        jibunAddress = this.jibunAddress,
    )

    companion object {
        fun from(address: Address) = PromotionAddressJpaEmbeddable(
            sido = address.sido,
            sigungu = address.sigungu,
            roadAddress = address.roadAddress,
            jibunAddress = address.jibunAddress,
        )
    }
}
