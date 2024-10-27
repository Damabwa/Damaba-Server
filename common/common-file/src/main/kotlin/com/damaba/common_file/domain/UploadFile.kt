package com.damaba.common_file.domain

import java.io.InputStream

data class UploadFile(
    val name: String?,
    val size: Long,
    val contentType: String?,
    val inputStream: InputStream,
)
