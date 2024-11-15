package com.damaba.file.util

import com.damaba.common_file.domain.File
import com.damaba.file.domain.UploadFile
import com.damaba.file.util.RandomTestUtils.Companion.randomLong
import com.damaba.file.util.RandomTestUtils.Companion.randomString
import org.springframework.mock.web.MockMultipartFile
import org.springframework.web.multipart.MultipartFile

object FileFixture {
    fun createUploadFile(
        name: String? = randomString(),
    ): UploadFile = UploadFile(
        name = name,
        size = randomLong(positive = true),
        contentType = "jpg",
        inputStream = randomString().byteInputStream(),
    )

    fun createFile(
        name: String = randomString(),
        url: String = randomString(),
    ) = File(name, url)

    fun createMockMultipartFile(): MultipartFile =
        MockMultipartFile(randomString(), randomString().byteInputStream())
}
