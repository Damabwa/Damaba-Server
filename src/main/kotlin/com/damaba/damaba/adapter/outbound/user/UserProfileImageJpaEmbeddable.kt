package com.damaba.damaba.adapter.outbound.user

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
data class UserProfileImageJpaEmbeddable(
    @Column(name = "profile_image_name", nullable = false)
    val name: String,

    @Column(name = "profile_image_url", nullable = false)
    val url: String,
)
