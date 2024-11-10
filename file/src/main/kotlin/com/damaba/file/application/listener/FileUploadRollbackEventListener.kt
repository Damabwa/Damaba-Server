package com.damaba.file.application.listener

import com.damaba.common_file.domain.FileUploadRollbackEvent
import com.damaba.file.application.port.outbound.DeleteFilePort
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
