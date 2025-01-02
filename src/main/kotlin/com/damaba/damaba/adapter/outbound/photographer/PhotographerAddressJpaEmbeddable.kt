package com.damaba.damaba.adapter.outbound.photographer

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
)
