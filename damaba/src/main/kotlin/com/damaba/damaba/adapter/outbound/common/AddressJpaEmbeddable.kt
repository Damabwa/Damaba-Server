package com.damaba.damaba.adapter.outbound.common

import com.damaba.damaba.domain.common.Address
import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
data class AddressJpaEmbeddable(
    @Column(name = "sido", nullable = false)
    val sido: String,

    @Column(name = "sigungu", nullable = false)
    val sigungu: String,

    @Column(name = "road_address")
    val roadAddress: String,

    @Column(name = "jibun_address")
    val jibunAddress: String,
) {
    fun toDomain() = Address(
        sido = this.sido,
        sigungu = this.sigungu,
        roadAddress = this.roadAddress,
        jibunAddress = this.jibunAddress,
    )

    companion object {
        fun from(address: Address) = AddressJpaEmbeddable(
            sido = address.sido,
            sigungu = address.sigungu,
            roadAddress = address.roadAddress,
            jibunAddress = address.jibunAddress,
        )
    }
}
