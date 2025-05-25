package com.damaba.damaba.application.file

import com.damaba.damaba.application.port.inbound.file.UploadFilesUseCase
import com.damaba.damaba.domain.file.File
import com.damaba.damaba.infrastructure.file.FileStorageManager
import org.springframework.stereotype.Service

@Service
class FileService(
    private val fileStorageManager: FileStorageManager,
) : UploadFilesUseCase {
    override fun uploadFiles(command: UploadFilesUseCase.Command): List<File> = fileStorageManager.upload(command.files, command.fileType.uploadPath)
}
