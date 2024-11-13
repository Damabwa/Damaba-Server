package com.damaba.damaba.adapter.outbound.common

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
)
