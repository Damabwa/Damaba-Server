package com.damaba.damaba.infrastructure.photographer

import com.damaba.damaba.domain.common.Address
import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
data class PhotographerAddressJpaEmbeddable(
    @Column(name = "sido", nullable = true)
    val sido: String,

    @Column(name = "sigungu", nullable = true)
    val sigungu: String,

    @Column(name = "road_address", nullable = true)
    val roadAddress: String,

    @Column(name = "jibun_address", nullable = true)
    val jibunAddress: String,
) {
    fun toAddress() = Address(
        sido = this.sido,
        sigungu = this.sigungu,
        roadAddress = this.roadAddress,
        jibunAddress = this.jibunAddress,
    )

    companion object {
        fun from(address: Address) = PhotographerAddressJpaEmbeddable(
            sido = address.sido,
            sigungu = address.sigungu,
            roadAddress = address.roadAddress,
            jibunAddress = address.jibunAddress,
        )
    }
}
