package com.damaba.damaba.adapter.outbound.common

import com.damaba.damaba.domain.common.Address
import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
class AddressJpaEntity(
    sido: String,
    sigungu: String,
    roadAddress: String?,
    jibunAddress: String?,
) {
    @Column(name = "sido", nullable = false)
    var sido: String = sido
        private set

    @Column(name = "sigungu", nullable = false)
    var sigungu: String = sigungu
        private set

    @Column(name = "road_address")
    var roadAddress: String? = roadAddress
        private set

    @Column(name = "jibun_address")
    var jibunAddress: String? = jibunAddress
        private set

    fun toDomain() = Address(
        sido = this.sido,
        sigungu = this.sigungu,
        roadAddress = this.roadAddress,
        jibunAddress = this.jibunAddress,
    )

    companion object {
        fun from(address: Address) = AddressJpaEntity(
            sido = address.sido,
            sigungu = address.sigungu,
            roadAddress = address.roadAddress,
            jibunAddress = address.jibunAddress,
        )
    }
}
