package com.damaba.file.application.port.inbound

import com.damaba.common_file.domain.File
import com.damaba.file.domain.FileType
import com.damaba.file.domain.UploadFile

interface UploadFilesUseCase {
    fun uploadFiles(command: Command): List<File>

    data class Command(
        val fileType: FileType,
        val files: List<UploadFile>,
    )
}
