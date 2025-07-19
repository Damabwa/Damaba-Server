package com.damaba.damaba.util.fixture

import com.damaba.damaba.controller.region.RegionRequest
import com.damaba.damaba.domain.region.Region
import com.damaba.damaba.domain.region.RegionCluster
import com.damaba.damaba.domain.region.RegionGroup
import com.damaba.damaba.util.RandomTestUtils.Companion.generateRandomList
import com.damaba.damaba.util.RandomTestUtils.Companion.randomString

object RegionFixture {
    fun createRegionRequest(
        category: String = randomString(),
        name: String = randomString(),
    ) = RegionRequest(category, name)

    fun createRegion(
        category: String = randomString(),
        name: String = randomString(),
    ) = Region(category, name)

    private fun createRegionGroup(): RegionGroup = RegionGroup(
        category = randomString(),
        regions = generateRandomList(maxSize = 10) { randomString() },
    )

    fun createRegionGroups(): List<RegionGroup> = generateRandomList(maxSize = 10) { createRegionGroup() }

    private fun createRegionCluster(): RegionCluster = RegionCluster(
        category = randomString(),
        clusters = generateRandomList(maxSize = 10) { randomString() },
    )

    fun createRegionClusters(): List<RegionCluster> = generateRandomList(maxSize = 10) { createRegionCluster() }
}
