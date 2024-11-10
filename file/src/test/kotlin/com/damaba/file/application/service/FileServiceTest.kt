package com.damaba.file.application.service

import com.damaba.file.application.port.inbound.UploadFilesUseCase
import com.damaba.file.application.port.outbound.UploadFilesPort
import com.damaba.file.domain.FileType
import com.damaba.file.util.RandomTestUtils.Companion.generateRandomList
import com.damaba.file.util.TestFixture.createFile
import com.damaba.file.util.TestFixture.createUploadFile
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import kotlin.test.Test

class FileServiceTest {
    private val uploadFilesPort: UploadFilesPort = mockk()
    private val sut = FileService(uploadFilesPort)

    private fun confirmVerifiedEveryMocks() {
        confirmVerified(uploadFilesPort)
    }

    @Test
    fun `업로드할 파일 유형과 파일들이 주어지고, 주어진 파일들을 업로드한다`() {
        // given
        val command = UploadFilesUseCase.Command(
            fileType = FileType.PROMOTION_IMAGE,
            files = generateRandomList(maxSize = 10) { createUploadFile() },
        )
        val expectedResult = List(command.files.size) { createFile() }
        every { uploadFilesPort.upload(command.files, command.fileType.uploadPath) } returns expectedResult

        // when
        val actualResult = sut.uploadFiles(command)

        // then
        verify { uploadFilesPort.upload(command.files, command.fileType.uploadPath) }
        confirmVerifiedEveryMocks()
        assertThat(actualResult).isEqualTo(expectedResult)
    }
}
