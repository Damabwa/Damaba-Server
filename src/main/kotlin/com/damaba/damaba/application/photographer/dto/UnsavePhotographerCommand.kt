package com.damaba.damaba.application.photographer.dto

data class UnsavePhotographerCommand(
    val requestUserId: Long,
    val photographerId: Long,
)
