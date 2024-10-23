package com.damaba.user.infrastructure.file

import com.damaba.user.domain.file.FileStorageRepository
import com.damaba.user.domain.file.FileUploadRollbackEvent
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class FileUploadRollbackEventListener(private val fileStorageRepository: FileStorageRepository) {
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    fun handle(event: FileUploadRollbackEvent) {
        event.uploadedFiles.forEach { uploadedFile ->
            fileStorageRepository.delete(uploadedFile)
        }
    }
}
