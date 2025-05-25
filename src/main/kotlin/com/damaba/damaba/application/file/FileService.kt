package com.damaba.damaba.application.file

import com.damaba.damaba.application.file.dto.UploadFilesCommand
import com.damaba.damaba.domain.file.File
import com.damaba.damaba.infrastructure.file.FileStorageManager
import org.springframework.stereotype.Service

@Service
class FileService(private val fileStorageManager: FileStorageManager) {
    fun uploadFiles(command: UploadFilesCommand): List<File> = fileStorageManager.upload(command.files, command.fileType.uploadPath)
}
