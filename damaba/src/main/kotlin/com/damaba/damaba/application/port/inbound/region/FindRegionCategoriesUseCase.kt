package com.damaba.damaba.application.port.inbound.region

interface FindRegionCategoriesUseCase {
    fun findRegionCategories(): List<String>
}
