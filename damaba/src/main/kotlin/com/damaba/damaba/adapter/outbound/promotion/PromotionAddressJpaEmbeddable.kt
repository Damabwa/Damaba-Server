package com.damaba.damaba.adapter.outbound.promotion

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
)
