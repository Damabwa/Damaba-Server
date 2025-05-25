package com.damaba.damaba.application.file.dto

import com.damaba.damaba.domain.file.FileType
import com.damaba.damaba.domain.file.UploadFile

data class UploadFilesCommand(
    val fileType: FileType,
    val files: List<UploadFile>,
)
