package com.damaba.damaba.controller.file

import com.damaba.damaba.application.file.FileService
import com.damaba.damaba.application.file.dto.UploadFilesCommand
import com.damaba.damaba.config.ControllerTestConfig
import com.damaba.damaba.controller.file.dto.UploadFilesRequest
import com.damaba.damaba.domain.file.FileType
import com.damaba.damaba.util.RandomTestUtils
import com.damaba.damaba.util.fixture.FileFixture.createFile
import com.damaba.damaba.util.fixture.FileFixture.createMockMultipartFile
import com.damaba.damaba.util.fixture.SecurityFixture.createAuthenticationToken
import com.damaba.damaba.util.fixture.UserFixture.createUser
import io.mockk.every
import io.mockk.mockk
import org.hamcrest.Matchers.hasSize
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import kotlin.test.Test

@ActiveProfiles("test")
@Import(ControllerTestConfig::class)
@WebMvcTest(controllers = [FileController::class])
class FileControllerTest @Autowired constructor(
    private val mvc: MockMvc,
    private val fileService: FileService,
) {
    @TestConfiguration
    class TestBeanSetUp {
        @Bean
        fun fileService(): FileService = mockk()
    }

    @Test
    fun `파일들을 업로드한다`() {
        // given
        val request = UploadFilesRequest(
            fileType = FileType.USER_PROFILE_IMAGE,
            files = RandomTestUtils.generateRandomList(maxSize = 5) { createMockMultipartFile() },
        )
        val expectedResult = List(request.files.size) { createFile() }
        every {
            fileService.uploadFiles(any(UploadFilesCommand::class))
        } returns expectedResult

        // when and then
        val requestBuilder = multipart("/api/v1/files")
        request.files.forEach { file -> requestBuilder.file("files", file.bytes) }
        mvc.perform(
            requestBuilder
                .param("fileType", request.fileType.name)
                .with(authentication(createAuthenticationToken(createUser()))),
        ).andExpect(status().isCreated())
            .andExpect(jsonPath("$.files", hasSize<Int>(expectedResult.size)))
    }
}
