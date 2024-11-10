package com.damaba.file.domain

import java.io.InputStream

data class UploadFile(
    val name: String?,
    val size: Long,
    val contentType: String?,
    val inputStream: InputStream,
)
