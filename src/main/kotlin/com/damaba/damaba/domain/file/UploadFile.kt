package com.damaba.damaba.domain.file

import java.io.InputStream

data class UploadFile(
    val name: String?,
    val size: Long,
    val contentType: String?,
    val inputStream: InputStream,
)
