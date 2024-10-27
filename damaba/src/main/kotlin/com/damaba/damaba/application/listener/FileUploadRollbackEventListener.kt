package com.damaba.damaba.application.listener

import com.damaba.common_file.application.port.outbound.DeleteFilePort
import com.damaba.common_file.domain.FileUploadRollbackEvent
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
