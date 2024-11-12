package com.damaba.user.adapter.outbound.user

import com.damaba.common_file.domain.Image
import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
data class UserProfileImageJpaEmbeddable(
    @Column(name = "profile_image_name", nullable = false)
    val name: String,

    @Column(name = "profile_image_url", nullable = false)
    val url: String,
) {
    fun toDomain() = Image(name, url)

    companion object {
        fun from(userProfileImage: Image) = UserProfileImageJpaEmbeddable(
            name = userProfileImage.name,
            url = userProfileImage.url,
        )
    }
}
