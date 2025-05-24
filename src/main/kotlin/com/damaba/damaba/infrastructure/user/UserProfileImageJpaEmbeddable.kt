package com.damaba.damaba.infrastructure.user

import com.damaba.damaba.domain.file.Image
import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
data class UserProfileImageJpaEmbeddable(
    @Column(name = "profile_image_name", nullable = true)
    val name: String,

    @Column(name = "profile_image_url", nullable = true)
    val url: String,
) {
    fun toImage() = Image(name = this.name, url = this.url)

    companion object {
        fun from(image: Image) = UserProfileImageJpaEmbeddable(name = image.name, url = image.url)
    }
}
