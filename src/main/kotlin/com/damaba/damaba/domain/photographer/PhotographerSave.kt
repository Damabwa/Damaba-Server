package com.damaba.damaba.domain.photographer

data class PhotographerSave(
    val id: Long,
    val userId: Long,
    val photographerId: Long,
) {
    companion object {
        fun create(userId: Long, photographerId: Long) = PhotographerSave(
            id = 0,
            userId = userId,
            photographerId = photographerId,
        )
    }
}
