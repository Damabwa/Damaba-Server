package com.damaba.file.application.service

import com.damaba.common_file.domain.File
import com.damaba.file.application.port.inbound.UploadFilesUseCase
import com.damaba.file.application.port.outbound.UploadFilesPort
import org.springframework.stereotype.Service

@Service
class FileService(
    private val uploadFilesPort: UploadFilesPort,
) : UploadFilesUseCase {
    override fun uploadFiles(command: UploadFilesUseCase.Command): List<File> =
        uploadFilesPort.upload(command.files, command.fileType.uploadPath)
}
