package com.damaba.damaba.util.fixture

import com.damaba.common_file.domain.Image
import com.damaba.damaba.adapter.inbound.common.dto.ImageRequest
import com.damaba.damaba.util.RandomTestUtils.Companion.randomString

object FileFixture {
    fun createImageRequest(name: String = randomString(), url: String = randomString()) = ImageRequest(name, url)

    fun createImage(name: String = randomString(), url: String = randomString()) = Image(name, url)
}
