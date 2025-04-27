package com.damaba.damaba.application.port.inbound.file

import com.damaba.damaba.domain.file.File
import com.damaba.damaba.domain.file.FileType
import com.damaba.damaba.domain.file.UploadFile

interface UploadFilesUseCase {
    fun uploadFiles(command: Command): List<File>

    data class Command(
        val fileType: FileType,
        val files: List<UploadFile>,
    )
}
