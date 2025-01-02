package com.damaba.damaba.application.service.file

import com.damaba.damaba.application.port.inbound.file.UploadFilesUseCase
import com.damaba.damaba.application.port.outbound.file.UploadFilesPort
import com.damaba.damaba.domain.file.File
import org.springframework.stereotype.Service

@Service
class FileService(
    private val uploadFilesPort: UploadFilesPort,
) : UploadFilesUseCase {
    override fun uploadFiles(command: UploadFilesUseCase.Command): List<File> =
        uploadFilesPort.upload(command.files, command.fileType.uploadPath)
}
