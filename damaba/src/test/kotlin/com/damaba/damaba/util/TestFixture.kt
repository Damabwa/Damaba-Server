package com.damaba.damaba.util

import com.damaba.common_file.domain.UploadFile
import com.damaba.damaba.domain.region.RegionGroup
import com.damaba.damaba.util.RandomTestUtils.Companion.randomInt
import com.damaba.damaba.util.RandomTestUtils.Companion.randomLong
import com.damaba.damaba.util.RandomTestUtils.Companion.randomString

object TestFixture {
    fun createRegionGroup(): RegionGroup {
        val regions: MutableList<String> = mutableListOf()
        for (i in 1..randomInt(positive = true, max = 5)) {
            regions.add(randomString())
        }
        return RegionGroup(
            category = randomString(),
            regions = regions,
        )
    }

    fun createRegionGroups(): List<RegionGroup> {
        val regionGroups: MutableList<RegionGroup> = mutableListOf()
        for (i in 1..randomInt(positive = true, max = 10)) {
            regionGroups.add(createRegionGroup())
        }
        return regionGroups
    }

    fun createUploadFile(
        name: String? = randomString(),
    ): UploadFile = UploadFile(
        name = name,
        size = randomLong(positive = true),
        contentType = "jpg",
        inputStream = randomString().byteInputStream(),
    )
}
