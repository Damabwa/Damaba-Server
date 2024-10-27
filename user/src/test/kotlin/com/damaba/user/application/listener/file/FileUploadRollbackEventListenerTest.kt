package com.damaba.user.application.listener.file

import com.damaba.user.application.port.outbound.file.DeleteFilePort
import com.damaba.user.domain.file.FileUploadRollbackEvent
import com.damaba.user.domain.file.UploadedFile
import com.damaba.user.util.RandomTestUtils.Companion.randomString
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.Test

class FileUploadRollbackEventListenerTest {
    private val deleteFilePort: DeleteFilePort = mockk()
    private val sut: FileUploadRollbackEventListener = FileUploadRollbackEventListener(deleteFilePort)

    @Test
    fun `FileUploadRollbackEvent가 발생하면, 업로드되었던 파일들을 삭제한다`() {
        // given
        val uploadedFiles = listOf(
            UploadedFile(randomString(), randomString()),
            UploadedFile(randomString(), randomString()),
        )
        val event = FileUploadRollbackEvent(uploadedFiles)
        every { deleteFilePort.delete(any(UploadedFile::class)) } just Runs

        // when
        sut.handle(event)

        // then
        verify(exactly = 1) { deleteFilePort.delete(uploadedFiles[0]) }
        verify(exactly = 1) { deleteFilePort.delete(uploadedFiles[1]) }
    }
}
