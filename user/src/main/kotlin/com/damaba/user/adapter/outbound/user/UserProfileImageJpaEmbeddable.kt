package com.damaba.user.adapter.outbound.user

import com.damaba.user.domain.user.UserProfileImage
import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
data class UserProfileImageJpaEmbeddable(
    @Column(name = "profile_image_name", nullable = false)
    val name: String,

    @Column(name = "profile_image_url", nullable = false)
    val url: String,
) {
    fun toDomain() = UserProfileImage(name, url)

    companion object {
        fun from(userProfileImage: UserProfileImage) = UserProfileImageJpaEmbeddable(
            name = userProfileImage.name,
            url = userProfileImage.url,
        )
    }
}
