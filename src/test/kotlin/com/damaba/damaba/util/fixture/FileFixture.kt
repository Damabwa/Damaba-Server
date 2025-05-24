package com.damaba.damaba.util.fixture

import com.damaba.damaba.controller.common.request.ImageRequest
import com.damaba.damaba.domain.file.File
import com.damaba.damaba.domain.file.Image
import com.damaba.damaba.domain.file.UploadFile
import com.damaba.damaba.util.RandomTestUtils.Companion.randomLong
import com.damaba.damaba.util.RandomTestUtils.Companion.randomString
import com.damaba.damaba.util.RandomTestUtils.Companion.randomUrl
import org.springframework.mock.web.MockMultipartFile
import org.springframework.web.multipart.MultipartFile

object FileFixture {
    fun createMockMultipartFile(): MultipartFile = MockMultipartFile(randomString(), randomString().toByteArray())

    fun createFile(): File = File(name = randomString(), url = randomString())

    fun createUploadFile(name: String? = randomString()): UploadFile = UploadFile(
        name = name,
        size = randomLong(positive = true),
        contentType = "jpg",
        inputStream = randomString().byteInputStream(),
    )

    fun createImageRequest() = createImageRequest(randomString(), randomUrl())

    fun createImageRequest(name: String = randomString(), url: String = randomString()) = ImageRequest(name, url)

    fun createImage(name: String = randomString(), url: String = randomString()) = Image(name, url)
}
