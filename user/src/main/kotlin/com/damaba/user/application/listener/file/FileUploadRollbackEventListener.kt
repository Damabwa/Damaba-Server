package com.damaba.user.application.listener.file

import com.damaba.user.application.port.outbound.file.DeleteFilePort
import com.damaba.user.domain.file.FileUploadRollbackEvent
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class FileUploadRollbackEventListener(private val deleteFilePort: DeleteFilePort) {
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    fun handle(event: FileUploadRollbackEvent) {
        event.uploadedFiles.forEach { uploadedFile ->
            deleteFilePort.delete(uploadedFile)
        }
    }
}
