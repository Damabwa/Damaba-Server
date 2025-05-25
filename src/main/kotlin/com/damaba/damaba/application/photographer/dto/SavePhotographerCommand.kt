package com.damaba.damaba.application.photographer.dto

data class SavePhotographerCommand(
    val requestUserId: Long,
    val photographerId: Long,
)
