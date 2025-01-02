package com.damaba.damaba.application.listener.file

import com.damaba.damaba.application.port.outbound.file.DeleteFilePort
import com.damaba.damaba.domain.file.FileUploadRollbackEvent
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class FileUploadRollbackEventListener(private val deleteFilePort: DeleteFilePort) {
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    fun handle(event: FileUploadRollbackEvent) {
        event.uploadedFiles.forEach { file ->
            deleteFilePort.delete(file)
        }
    }
}
