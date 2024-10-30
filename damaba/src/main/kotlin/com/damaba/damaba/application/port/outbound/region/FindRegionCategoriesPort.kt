package com.damaba.damaba.application.port.outbound.region

interface FindRegionCategoriesPort {
    fun findRegionCategories(): List<String>
}
