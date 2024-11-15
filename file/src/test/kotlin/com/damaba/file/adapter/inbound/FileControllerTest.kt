package com.damaba.file.adapter.inbound

import com.damaba.file.adapter.inbound.dto.UploadFilesRequest
import com.damaba.file.application.port.inbound.UploadFilesUseCase
import com.damaba.file.domain.FileType
import com.damaba.file.util.FileFixture
import com.damaba.file.util.FileFixture.createFile
import com.damaba.file.util.RandomTestUtils
import io.mockk.every
import io.mockk.mockk
import org.hamcrest.Matchers.hasSize
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import kotlin.test.Test

@ActiveProfiles("test")
// @Import(ControllerTestConfig::class)
@WebMvcTest(controllers = [FileController::class])
class FileControllerTest @Autowired constructor(
    private val mvc: MockMvc,
    private val uploadFilesUseCase: UploadFilesUseCase,
) {
    @TestConfiguration
    class TestBeanSetUp {
        @Bean
        fun uploadFilesUseCase(): UploadFilesUseCase = mockk()
    }

    @Test
    fun `파일들을 업로드한다`() {
        // given
        val request = UploadFilesRequest(
            fileType = FileType.USER_PROFILE_IMAGE,
            files = RandomTestUtils.generateRandomList(maxSize = 5) { FileFixture.createMockMultipartFile() },
        )
        val expectedResult = List(request.files.size) { createFile() }
        every {
            uploadFilesUseCase.uploadFiles(any(UploadFilesUseCase.Command::class))
        } returns expectedResult

        // when & then
        val requestBuilder = multipart("/api/v1/files")
        request.files.forEach { file -> requestBuilder.file("files", file.bytes) }
        mvc.perform(
            requestBuilder
                .param("fileType", request.fileType.name),
        ).andExpect(status().isCreated())
            .andExpect(jsonPath("files", hasSize<Int>(expectedResult.size)))
    }
}
