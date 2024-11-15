package com.damaba.user.util

import com.damaba.common_file.domain.Image
import com.damaba.user.util.RandomTestUtils.Companion.randomString

object FileFixture {
    fun createImage(name: String = randomString(), url: String = randomString()) = Image(name, url)
}
