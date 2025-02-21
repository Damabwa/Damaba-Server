package com.damaba.damaba.domain.photographer

data class SavedPhotographer(
    val id: Long,
    val userId: Long,
    val photographerId: Long,
) {
    companion object {
        fun create(userId: Long, photographerId: Long) = SavedPhotographer(
            id = 0,
            userId = userId,
            photographerId = photographerId,
        )
    }
}
